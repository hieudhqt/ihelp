package com.swp.ihelp.app.service;

import com.swp.ihelp.app.entity.AccountEntity;
import com.swp.ihelp.app.entity.AccountRepository;
import com.swp.ihelp.app.point.PointEntity;
import com.swp.ihelp.app.point.PointRepository;
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
    public ServiceVolunteerServiceImpl(ServiceRepository serviceRepository, AccountRepository accountRepository, PointRepository pointRepository) {
        this.serviceRepository = serviceRepository;
        this.accountRepository = accountRepository;
        this.pointRepository = pointRepository;
    }

    @Override
    public List<ServiceResponse> findAll() throws Exception {
        List<ServiceEntity> serviceEntityList = serviceRepository.findAll();
        List<ServiceResponse> result = convertToResponseObject(serviceEntityList);
        return result;
    }

    @Override
    public ServiceResponse findById(String id) throws Exception {
        Optional<ServiceEntity> result = serviceRepository.findById(id);
        ServiceResponse service = null;
        if (result.isPresent()) {
            service = new ServiceResponse(result.get());
        } else {
            throw new RuntimeException("Did not find service with id:" + id);
        }
        return service;
    }

    @Override
    public List<ServiceResponse> findByTitle(String title) throws Exception {
        List<ServiceEntity> serviceEntityList = serviceRepository.findByTitle(title);
        List<ServiceResponse> result = convertToResponseObject(serviceEntityList);
        return result;
    }

    @Override
    public void save(ServiceEntity service) throws Exception {
        // Set createDate as current date for new event.
        if (service.getId() == null) {
            service.setCreatedDate(new Date().getTime());
        }

        serviceRepository.save(service);
    }

    @Override
    public void deleteById(String id) throws Exception {
        serviceRepository.deleteById(id);
    }

    @Override
    public List<ServiceResponse> findByServiceTypeId(int serviceTypeId) throws Exception {
        List<ServiceEntity> serviceEntityList = serviceRepository.findByServiceTypeId(serviceTypeId);
        List<ServiceResponse> result = convertToResponseObject(serviceEntityList);
        return result;
    }

    @Override
    public List<ServiceResponse> findByStatusId(int statusId) throws Exception {
        List<ServiceEntity> serviceEntityList = serviceRepository.findByServiceStatusId(statusId);
        List<ServiceResponse> result = convertToResponseObject(serviceEntityList);
        return result;
    }

    @Override
    public List<ServiceResponse> findByAuthorEmail(String email) throws Exception {
        List<ServiceEntity> serviceEntityList = serviceRepository.findByAuthorEmail(email);
        List<ServiceResponse> result = convertToResponseObject(serviceEntityList);
        return result;
    }

    //1. Get the service point
    //2. Reduce user's point balance
    //3. Add the point to author's point balance
    //4. Record the transaction in table "point"
    @Override
    public void useService(String email, String serviceId) throws Exception {
        Optional<ServiceEntity> serviceOptional = serviceRepository.findById(serviceId);
        ServiceEntity service = serviceOptional.get();

        Optional<AccountEntity> userAccountOptional = accountRepository.findById(email);
        AccountEntity userAccount = null;
        if (userAccountOptional.isPresent()) {
            userAccount = userAccountOptional.get();
        }

//            System.out.println(userAccount.convertToEntity().toString());
        ServiceHasAccountEntity serviceAccount = new ServiceHasAccountEntity();
        serviceAccount.setService(service);
        serviceAccount.setAccount(userAccount);

        service.getServiceAccount().add(serviceAccount);
//
        ServiceEntity savedService = serviceRepository.save(service);

        int servicePoint = service.getPoint();
//
        userAccount.setBalancePoint(userAccount.getBalancePoint() - servicePoint);
        accountRepository.save(userAccount);

        String authorEmail = service.getAuthorAccount().getEmail();
        Optional<AccountEntity> authorAccountOptional = accountRepository.findById(authorEmail);
        AccountEntity authorAccount = authorAccountOptional.get();
        authorAccount.setBalancePoint(authorAccount.getBalancePoint() + servicePoint);
        authorAccount.setCumulativePoint(authorAccount.getCumulativePoint() + servicePoint);
        accountRepository.save(authorAccount);
//
        savePoint(servicePoint, authorAccount, userAccount, service);
        AccountEntity savedAccount = accountRepository.save(userAccount);
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
}
