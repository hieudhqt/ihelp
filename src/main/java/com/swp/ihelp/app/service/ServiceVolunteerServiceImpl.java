package com.swp.ihelp.app.service;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.image.ImageEntity;
import com.swp.ihelp.app.image.ImageRepository;
import com.swp.ihelp.app.image.request.ImageRequest;
import com.swp.ihelp.app.point.PointEntity;
import com.swp.ihelp.app.point.PointRepository;
import com.swp.ihelp.app.service.request.CreateServiceRequest;
import com.swp.ihelp.app.service.request.UpdateServiceRequest;
import com.swp.ihelp.app.service.response.ServiceDetailResponse;
import com.swp.ihelp.app.service.response.ServiceResponse;
import com.swp.ihelp.app.servicejointable.ServiceHasAccountEntity;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceVolunteerServiceImpl implements ServiceVolunteerService {

    private ServiceRepository serviceRepository;
    private AccountRepository accountRepository;
    private PointRepository pointRepository;
    private ImageRepository imageRepository;

    @Value("${paging.page-size}")
    private int pageSize;

    @Value("${date.minStartDateFromCreate}")
    private long minStartDateFromCreate;

    @Autowired
    public void setServiceRepository(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired
    public void setPointRepository(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    @Autowired
    public void setImageRepository(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Map<String, Object> findAll(int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<ServiceEntity> pageServices = serviceRepository.findAll(paging);
        if (pageServices.isEmpty()) {
            throw new EntityNotFoundException("Service not found.");
        }
        Map<String, Object> response = getServiceResponseMap(pageServices);
        return response;
    }

    @Override
    public ServiceDetailResponse findById(String id) throws Exception {
        Optional<ServiceEntity> result = serviceRepository.findById(id);
        ServiceDetailResponse service = null;
        if (result.isPresent()) {
            service = new ServiceDetailResponse(result.get());
            int remainingSpots = serviceRepository.getUsedSpot(id);
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
    public Map<String, Object> findByTitle(String title, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<ServiceEntity> pageServices = serviceRepository.findByTitle(title, paging);
        if (pageServices.isEmpty()) {
            throw new EntityNotFoundException("Service with title: " + title + " not found.");
        }
        Map<String, Object> response = getServiceResponseMap(pageServices);
        return response;
    }

    @Override
    public Map<String, Object> findByCategoryId(int categoryId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<ServiceEntity> pageServices = serviceRepository
                .findByCategoryId(categoryId, paging);
        if (pageServices.isEmpty()) {
            throw new EntityNotFoundException("Service with category ID: " + categoryId + " not found.");
        }
        Map<String, Object> response = getServiceResponseMap(pageServices);
        return response;
    }

    @Override
    public Map<String, Object> findByStatusId(int statusId, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<ServiceEntity> pageServices = serviceRepository
                .findByServiceStatusId(statusId, paging);
        if (pageServices.isEmpty()) {
            throw new EntityNotFoundException("Service with status ID: " + statusId + " not found.");
        }
        Map<String, Object> response = getServiceResponseMap(pageServices);
        return response;
    }

    @Override
    public Map<String, Object> findByAuthorEmail(String email, int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<ServiceEntity> pageServices = serviceRepository
                .findByAuthorEmail(email, paging);

        Map<String, Object> response = getServiceResponseMap(pageServices);
        return response;
    }

    @Override
    public String insert(CreateServiceRequest request) throws Exception {
        if (request.getStartDate().after(request.getEndDate())) {
            throw new RuntimeException("Start date must be before end date.");
        }
        if ((request.getStartDate().getTime() - System.currentTimeMillis())
                < minStartDateFromCreate) {
            throw new RuntimeException("Start date must be at least 3 days after create date.");
        }
        ServiceEntity serviceEntity = CreateServiceRequest.convertToEntity(request);
        List<ImageRequest> imageRequests = request.getImages();
        if (imageRequests != null) {
            for (ImageRequest imageRequest : imageRequests) {
                ImageEntity imageEntity = ImageRequest.convertRequestToEntity(imageRequest);
                imageEntity.setAuthorAccount(serviceEntity.getAuthorAccount());
                ImageEntity savedImage = imageRepository.save(imageEntity);
                serviceEntity.addImage(savedImage);
            }
        }
        ServiceEntity savedService = serviceRepository.save(serviceEntity);
        return savedService.getId();
    }

    @Override
    @Transactional
    public void update(UpdateServiceRequest request) throws Exception {
        ServiceEntity entity = UpdateServiceRequest.convertToEntityWithId(request);
        serviceRepository.update(entity);
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

        if (service.getAuthorAccount().getEmail().equals(email)) {
            throw new RuntimeException("You cannot use your own service.");
        }

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

        userAccount.decreaseBalancePoint(servicePoint);
        authorAccount.addBalancePoint(servicePoint);
        authorAccount.addContributionPoint(servicePoint);
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
        if (service.getStartDate().getTime() > currentDateInMillis
                || service.getEndDate().getTime() < currentDateInMillis) {
            check = false;
        }
        int remainingSpots = serviceRepository.getUsedSpot(service.getId());
        if (remainingSpots == service.getQuota()) {
            check = false;
        }
        return check;
    }

    private void savePoint(int amount, AccountEntity authorAccount, AccountEntity userAccount, ServiceEntity serviceEntity) throws Exception {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        PointEntity senderPoint = new PointEntity();
        senderPoint.setAmount(amount);
        senderPoint.setAccount(userAccount);
        senderPoint.setCreatedDate(currentTimestamp);
        senderPoint.setIsReceived(false);
        senderPoint.setDescription("Account " + userAccount.getEmail() +
                " used service: " + serviceEntity.getId());
        senderPoint.setService(serviceEntity);

        PointEntity receiverPoint = new PointEntity();
        receiverPoint.setAmount(amount);
        receiverPoint.setAccount(authorAccount);
        receiverPoint.setCreatedDate(currentTimestamp);
        receiverPoint.setIsReceived(true);
        receiverPoint.setDescription("Account " + authorAccount.getEmail() +
                " received point for providing service: " + serviceEntity.getId());
        receiverPoint.setService(serviceEntity);

        pointRepository.save(senderPoint);
        pointRepository.save(receiverPoint);
    }

    private List<ServiceResponse> convertEntitesToResponses(List<ServiceEntity> serviceEntityList)
            throws Exception {
        List<ServiceResponse> result = ServiceResponse.convertToResponseList(serviceEntityList);
        for (ServiceResponse response : result) {
            int remainingSpot = serviceRepository.getUsedSpot(response.getId());
            int quota = serviceRepository.getQuota(response.getId());
            if (quota >= remainingSpot) {
                response.setSpot(quota - remainingSpot);
            } else {
                response.setSpot(0);
            }
        }
        return result;
    }

    private Map<String, Object> getServiceResponseMap(Page<ServiceEntity> pageServices) throws Exception {
        List<ServiceEntity> serviceEntityList = pageServices.getContent();
        List<ServiceResponse> serviceResponses = convertEntitesToResponses(serviceEntityList);

        Map<String, Object> response = new HashMap<>();
        response.put("services", serviceResponses);
        response.put("currentPage", pageServices.getNumber());
        response.put("totalItems", pageServices.getTotalElements());
        response.put("totalPages", pageServices.getTotalPages());

        return response;
    }
}
