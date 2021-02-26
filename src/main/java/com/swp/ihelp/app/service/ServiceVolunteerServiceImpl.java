package com.swp.ihelp.app.service;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.point.PointEntity;
import com.swp.ihelp.app.point.PointRepository;
import com.swp.ihelp.app.service.mapper.ServiceMapper;
import com.swp.ihelp.app.service.request.ServiceRequest;
import com.swp.ihelp.app.service.response.ServiceDetailResponse;
import com.swp.ihelp.app.service.response.ServiceResponse;
import com.swp.ihelp.app.servicejointable.ServiceHasAccountEntity;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceVolunteerServiceImpl implements ServiceVolunteerService {

    private ServiceRepository serviceRepository;
    private AccountRepository accountRepository;
    private PointRepository pointRepository;

    @Autowired
    private ServiceMapper mapper;

//    @Value("${date.minStartDateFromCreate}")
//    private int minStartDate;

    @Autowired
    public ServiceVolunteerServiceImpl(ServiceRepository serviceRepository, AccountRepository accountRepository, PointRepository pointRepository) {
        this.serviceRepository = serviceRepository;
        this.accountRepository = accountRepository;
        this.pointRepository = pointRepository;
    }

    @Override
    public List<ServiceResponse> findAll() throws Exception {
        List<ServiceEntity> serviceEntityList = serviceRepository.findAll();
        return getServiceResponses(serviceEntityList);
    }

    @Override
    public ServiceDetailResponse findById(String id) throws Exception {
        Optional<ServiceEntity> result = serviceRepository.findById(id);
        ServiceDetailResponse service = null;
        if (result.isPresent()) {
            service = new ServiceDetailResponse(result.get());
            int remainingSpots = serviceRepository.getRemainingSpots(id);
            int quota = service.getQuota();
            if (quota >= remainingSpots) {
                service.setSpot(quota - remainingSpots);
            } else {
                service.setSpot(0);
            }
        } else {
            throw new RuntimeException("Did not find service with id:" + id);
        }
        return service;
    }

    @Override
    public List<ServiceResponse> findByTitle(String title) throws Exception {
        List<ServiceEntity> serviceEntityList = serviceRepository.findByTitle(title);
        return getServiceResponses(serviceEntityList);
    }

    @Override
    public List<ServiceResponse> findByServiceTypeId(int serviceTypeId) throws Exception {
        List<ServiceEntity> serviceEntityList = serviceRepository.findByServiceTypeId(serviceTypeId);
        return getServiceResponses(serviceEntityList);
    }

    @Override
    public List<ServiceResponse> findByStatusId(int statusId) throws Exception {
        List<ServiceEntity> serviceEntityList = serviceRepository.findByServiceStatusId(statusId);
        return getServiceResponses(serviceEntityList);
    }

    @Override
    public List<ServiceResponse> findByAuthorEmail(String email) throws Exception {
        List<ServiceEntity> serviceEntityList = serviceRepository.findByAuthorEmail(email);
        return getServiceResponses(serviceEntityList);
    }

    @Override
    public void insert(ServiceRequest request) throws Exception {
        ServiceEntity serviceEntity = ServiceRequest.convertToEntity(request);
        if (serviceEntity.getStartDate() >= serviceEntity.getEndDate()) {
            throw new RuntimeException("Start date must be before end date.");
        }
//        if (serviceEntity.getStartDate() < minStartDate) {
//            throw new RuntimeException("Start date must be at least 3 days after create date.");
//        }

        serviceRepository.save(serviceEntity);
    }

    @Override
    public void patch(ServiceRequest request) throws Exception {
        ServiceEntity entity = serviceRepository.getOne(request.getId());
        mapper.updateServiceFromRequest(request, entity);
        serviceRepository.save(entity);
    }

    @Override
    public void update(ServiceRequest request) throws Exception {
        ServiceEntity entity = ServiceRequest.convertToEntityWithId(request);
        serviceRepository.save(entity);
    }

    @Override
    public void deleteById(String id) throws Exception {
        serviceRepository.deleteById(id);
    }

    //1. Get the service point
    //2. Reduce user's point balance
    //3. Add the point to author's point balance
    //4. Record the transaction in table "point"
    @Override
    public void useService(String email, String serviceId) throws Exception {

        Optional<ServiceEntity> serviceOptional = serviceRepository.findById(serviceId);
        ServiceEntity service = serviceOptional.get();

        boolean check = isServiceAvailable(service, System.currentTimeMillis());
        if (!check) {
            return;
        }

        Optional<AccountEntity> userAccountOptional = accountRepository.findById(email);
        AccountEntity userAccount = null;
        if (userAccountOptional.isPresent()) {
            userAccount = userAccountOptional.get();
        }

        String authorEmail = service.getAuthorAccount().getEmail();
        Optional<AccountEntity> authorAccountOptional = accountRepository.findById(authorEmail);
        AccountEntity authorAccount = null;
        if (authorAccountOptional.isPresent()) {
            authorAccount = authorAccountOptional.get();
        }

        ServiceHasAccountEntity serviceAccount = new ServiceHasAccountEntity();
        serviceAccount.setService(service);
        serviceAccount.setAccount(userAccount);

        service.getServiceAccount().add(serviceAccount);

        serviceRepository.save(service);

        int servicePoint = service.getPoint();

        userAccount.setBalancePoint(userAccount.getBalancePoint() - servicePoint);
        authorAccount.setBalancePoint(authorAccount.getBalancePoint() + servicePoint);
        authorAccount.setCumulativePoint(authorAccount.getCumulativePoint() + servicePoint);
        accountRepository.save(authorAccount);
        accountRepository.save(userAccount);

        savePoint(servicePoint, authorAccount, userAccount, service);
    }

    // 1. Check if the service's status is "Approved".
    // 2. Compare the service's start and end date to current date.
    // 3. Check if the service still has room to use.
    private boolean isServiceAvailable(ServiceEntity service, long currentDateInMillis) throws Exception {
        boolean check = true;
        if (!service.getStatus().getName().equals("Approved")) {
            check = false;
        }
        if (service.getStartDate() > currentDateInMillis
                || service.getEndDate() < currentDateInMillis) {
            check = false;
        }
        int remainingSpots = serviceRepository.getRemainingSpots(service.getId());
        if (remainingSpots < 1) {
            check = false;
        }
        return check;
    }

    private void savePoint(int amount, AccountEntity authorAccount, AccountEntity userAccount, ServiceEntity serviceEntity) throws Exception {
        PointEntity senderPoint = new PointEntity();
        senderPoint.setAmount(amount);
        senderPoint.setAccount(userAccount);
        senderPoint.setCreatedDate(new Date().getTime());
        senderPoint.setIsReceived(false);
        senderPoint.setDescription("Account " + userAccount.getEmail() + " used service: " + serviceEntity.getId());
        senderPoint.setService(serviceEntity);

        PointEntity receiverPoint = new PointEntity();
        receiverPoint.setAmount(amount);
        receiverPoint.setAccount(authorAccount);
        receiverPoint.setCreatedDate(new Date().getTime());
        receiverPoint.setIsReceived(true);
        receiverPoint.setDescription("Account " + authorAccount.getEmail() + " received point for providing service: " + serviceEntity.getId());
        receiverPoint.setService(serviceEntity);

        pointRepository.save(senderPoint);
        pointRepository.save(receiverPoint);
    }

    private List<ServiceResponse> convertToResponseObject(List<ServiceEntity> serviceEntityList)
            throws Exception {
        if (serviceEntityList.isEmpty()) {
            throw new EntityNotFoundException("No service found.");
        }
        return ServiceResponse.convertToResponseList(serviceEntityList);
    }

    private List<ServiceDetailResponse> getServiceDetailResponses(List<ServiceEntity> serviceEntityList) throws Exception {
        List<ServiceDetailResponse> result = ServiceDetailResponse.convertToResponseList(serviceEntityList);
        for (ServiceDetailResponse response : result) {
            int remainingSpots = serviceRepository.getRemainingSpots(response.getId());
            int quota = response.getQuota();
            if (quota >= remainingSpots) {
                response.setSpot(quota - remainingSpots);
            } else {
                response.setSpot(0);
            }
        }
        return result;
    }

    private List<ServiceResponse> getServiceResponses(List<ServiceEntity> serviceEntityList) throws Exception {
        List<ServiceResponse> result = convertToResponseObject(serviceEntityList);
        for (ServiceResponse response : result) {
            int remainingSpots = serviceRepository.getRemainingSpots(response.getId());
            int quota = serviceRepository.getQuota(response.getId());
            if (quota >= remainingSpots) {
                response.setSpot(quota - remainingSpots);
            } else {
                response.setSpot(0);
            }
        }
        return result;
    }
}
