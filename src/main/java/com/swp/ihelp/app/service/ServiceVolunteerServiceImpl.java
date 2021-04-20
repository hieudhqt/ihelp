package com.swp.ihelp.app.service;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.image.ImageEntity;
import com.swp.ihelp.app.image.ImageRepository;
import com.swp.ihelp.app.image.request.ImageRequest;
import com.swp.ihelp.app.point.PointEntity;
import com.swp.ihelp.app.point.PointRepository;
import com.swp.ihelp.app.reward.RewardRepository;
import com.swp.ihelp.app.service.request.CreateServiceRequest;
import com.swp.ihelp.app.service.request.RejectServiceRequest;
import com.swp.ihelp.app.service.request.UpdateServiceRequest;
import com.swp.ihelp.app.service.response.ServiceDetailResponse;
import com.swp.ihelp.app.service.response.ServiceDistanceResponse;
import com.swp.ihelp.app.service.response.ServiceResponse;
import com.swp.ihelp.app.servicecategory.ServiceCategoryEntity;
import com.swp.ihelp.app.servicecategory.ServiceCategoryRepository;
import com.swp.ihelp.app.servicejointable.ServiceHasAccountEntity;
import com.swp.ihelp.app.status.StatusEntity;
import com.swp.ihelp.app.status.StatusEnum;
import com.swp.ihelp.app.status.StatusRepository;
import com.swp.ihelp.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ServiceVolunteerServiceImpl implements ServiceVolunteerService {

    private ServiceRepository serviceRepository;
    private AccountRepository accountRepository;
    private PointRepository pointRepository;
    private ImageRepository imageRepository;
    private ServiceCategoryRepository categoryRepository;
    private RewardRepository rewardRepository;
    private StatusRepository statusRepository;

    @Value("${paging.page-size}")
    private int pageSize;

    @Value("${date.service.minStartDateFromCreate}")
    private long minStartDateFromCreate;

    @Value("${pattern.search-filter}")
    private String filterPattern;

    @Autowired
    public ServiceVolunteerServiceImpl(ServiceRepository serviceRepository, AccountRepository accountRepository, PointRepository pointRepository, ImageRepository imageRepository, ServiceCategoryRepository categoryRepository, RewardRepository rewardRepository, StatusRepository statusRepository) {
        this.serviceRepository = serviceRepository;
        this.accountRepository = accountRepository;
        this.pointRepository = pointRepository;
        this.imageRepository = imageRepository;
        this.categoryRepository = categoryRepository;
        this.rewardRepository = rewardRepository;
        this.statusRepository = statusRepository;
    }

    @Override
    public Map<String, Object> findAll(int page) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("startDate").descending().and(Sort.by("id").ascending()));
        Page<ServiceEntity> pageServices = serviceRepository.findAll(paging);
        if (pageServices.isEmpty()) {
            throw new EntityNotFoundException("Service not found.");
        }
        Map<String, Object> response = getServiceResponseMap(pageServices);
        return response;
    }

    @Override
    public Map<String, Object> findAll(int page, String search) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize,
                Sort.by("startDate").descending().and(Sort.by("id").ascending()));

        ServiceSpecificationBuilder builder = new ServiceSpecificationBuilder();
        Pattern pattern = Pattern.compile(filterPattern);
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        Specification<ServiceEntity> spec = builder.build();

        Page<ServiceEntity> pageServices = serviceRepository.findAll(spec, paging);
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
            int remainingSpots = serviceRepository.getRemainingSpot(id);
            service.setSpot(remainingSpots);
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
    public Map<String, Object> findNearbyServices(int page, float radius, double lat, double lng) throws Exception {
        Pageable paging = PageRequest.of(page, pageSize);
        Page<Object[]> pageServices
                = serviceRepository.getNearbyServices(radius, lat, lng, StatusEnum.APPROVED.getId(), paging);

        if (pageServices.isEmpty()) {
            throw new EntityNotFoundException("Service not found.");
        }
        List<Object[]> listServices = pageServices.getContent();
        List<ServiceDistanceResponse> serviceResponses = new ArrayList<>();
        for (Object[] obj : listServices) {
            ServiceEntity service = serviceRepository.getOne((String) obj[0]);
            ServiceDistanceResponse serviceDistanceResponse = new ServiceDistanceResponse(service);
            serviceDistanceResponse.setDistance((double) obj[1]);
            int spot = serviceRepository.getRemainingSpot(service.getId());
            serviceDistanceResponse.setSpot(spot);
            serviceResponses.add(serviceDistanceResponse);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("services", serviceResponses);
        response.put("currentPage", pageServices.getNumber());
        response.put("totalItems", pageServices.getTotalElements());
        response.put("totalPages", pageServices.getTotalPages());
        return response;
    }

    @Override
    public String insert(CreateServiceRequest request) throws Exception {
        if (request.getStartDate().after(request.getEndDate())) {
            throw new RuntimeException("Start date must be before end date.");
        }
        if ((request.getStartDate().getTime() - System.currentTimeMillis())
                < minStartDateFromCreate) {
            throw new RuntimeException("Start date must be at least " + minStartDateFromCreate +
                    " days after create date.");
        }
        ServiceEntity serviceEntity = CreateServiceRequest.convertToEntity(request);
        Set<ImageRequest> imageRequests = request.getImages();
        if (imageRequests != null) {
            for (ImageRequest imageRequest : imageRequests) {
                ImageEntity imageEntity = ImageRequest.convertRequestToEntity(imageRequest);
                imageEntity.setAuthorAccount(serviceEntity.getAuthorAccount());
                serviceEntity.addImage(imageEntity);
            }
        }
        ServiceEntity savedService = serviceRepository.save(serviceEntity);
        return savedService.getId();
    }

    @Override
    @Transactional
    public ServiceDetailResponse update(UpdateServiceRequest serviceRequest) throws Exception {
        if (!serviceRepository.existsById(serviceRequest.getId())) {
            throw new EntityNotFoundException("Service with ID: "
                    + serviceRequest.getId() + " not found.");
        }
        ServiceEntity serviceToUpdate = serviceRepository.getOne(serviceRequest.getId());
        serviceToUpdate.setTitle(serviceRequest.getTitle());
        serviceToUpdate.setDescription(serviceRequest.getDescription());
        serviceToUpdate.setLocation(serviceRequest.getLocation());
        serviceToUpdate.setLatitude(serviceRequest.getLatitude());
        serviceToUpdate.setLongitude(serviceRequest.getLongitude());
        serviceToUpdate.setPoint(serviceRequest.getPoint());
        serviceToUpdate.setQuota(serviceRequest.getQuota());
        serviceToUpdate.setStartDate(new Timestamp(serviceRequest.getStartDate().getTime()));
        serviceToUpdate.setEndDate(new Timestamp(serviceRequest.getEndDate().getTime()));

        if (serviceRequest.getCategoryIds() != null) {
            Set<ServiceCategoryEntity> categoriesToUpdate = new HashSet<>();
            for (int categoryId : serviceRequest.getCategoryIds()) {
                categoriesToUpdate.add(categoryRepository.findById(categoryId).get());
            }
            serviceToUpdate.setServiceCategories(categoriesToUpdate);
        }

        serviceRepository.save(serviceToUpdate);

        ServiceDetailResponse serviceResponse = new ServiceDetailResponse(serviceToUpdate);
        int remainingSpot = serviceRepository.getRemainingSpot(serviceResponse.getId());
        serviceResponse.setSpot(remainingSpot);

        return serviceResponse;
    }

    @Override
    @Transactional
    public void updateStatus(String serviceId, int statusId) throws Exception {
        if (!serviceRepository.existsById(serviceId)) {
            throw new EntityNotFoundException("Service with ID:" + serviceId + " not found.");
        }
        if (!statusRepository.existsById(statusId)) {
            throw new EntityNotFoundException("Status with ID:" + statusId + " not found.");
        }

        serviceRepository.updateStatus(serviceId, statusId);
    }

    @Override
    public ServiceEntity approve(String serviceId, String managerEmail) throws Exception {
        if (!serviceRepository.existsById(serviceId)) {
            throw new EntityNotFoundException("Service with ID:" + serviceId + " not found.");
        }
        if (!accountRepository.existsById(managerEmail)) {
            throw new EntityNotFoundException("Account " + managerEmail + " not found.");
        }

        ServiceEntity serviceEntity = serviceRepository.getOne(serviceId);

        if (serviceEntity.getStatus().getId() != StatusEnum.PENDING.getId()) {
            throw new RuntimeException("You can only approve or reject service if it is pending");
        }
        if (serviceEntity.getAuthorAccount().getEmail().equals(managerEmail)) {
            throw new RuntimeException("You cannot approve or reject your own service.");
        }

        AccountEntity approver = accountRepository.getOne(managerEmail);

        serviceEntity.setManagerAccount(approver);
        serviceEntity.setStatus(new StatusEntity().setId(StatusEnum.APPROVED.getId()));
        ServiceEntity updatedService = serviceRepository.save(serviceEntity);
        return updatedService;
    }

    @Override
    public ServiceEntity reject(RejectServiceRequest request) throws Exception {
        String serviceId = request.getServiceId();
        String managerEmail = request.getManagerEmail();

        if (!serviceRepository.existsById(serviceId)) {
            throw new EntityNotFoundException("Event with ID:" + serviceId + " not found.");
        }
        if (!accountRepository.existsById(managerEmail)) {
            throw new EntityNotFoundException("Account " + managerEmail + " not found.");
        }

        ServiceEntity serviceEntity = serviceRepository.getOne(serviceId);

        if (serviceEntity.getStatus().getId() != StatusEnum.PENDING.getId()) {
            throw new RuntimeException("You can only approve or reject service if it is pending");
        }
        if (serviceEntity.getAuthorAccount().getEmail().equals(managerEmail)) {
            throw new RuntimeException("You cannot approve or reject your own service.");
        }

        AccountEntity rejecter = accountRepository.getOne(managerEmail);

        serviceEntity.setManagerAccount(rejecter);
        serviceEntity.setStatus(new StatusEntity().setId(StatusEnum.REJECTED.getId()));
        serviceEntity.setReason(request.getReason());
        ServiceEntity updatedService = serviceRepository.save(serviceEntity);
        return updatedService;
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
    public ServiceEntity useService(String email, String serviceId) throws Exception {

        Optional<ServiceEntity> serviceOptional = serviceRepository.findById(serviceId);
        ServiceEntity service = serviceOptional.get();

        if (service.getAuthorAccount().getEmail().equals(email)) {
            throw new RuntimeException("You cannot use your own service.");
        }

        Optional<AccountEntity> userAccountOptional = accountRepository.findById(email);
        AccountEntity userAccount = null;
        if (userAccountOptional.isPresent()) {
            userAccount = userAccountOptional.get();
        }

        String errorMsg = validateUseService(service, userAccount, System.currentTimeMillis());
        if (!errorMsg.isEmpty()) {
            throw new RuntimeException(errorMsg);
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

        service.addServiceAccount(serviceAccount);

        ServiceEntity updatedService = serviceRepository.save(service);

        if (service.getPoint() != null) {
            int servicePoint = service.getPoint();
            if (servicePoint > 0) {
                userAccount.decreaseBalancePoint(servicePoint);
                authorAccount.addBalancePoint(servicePoint);
                authorAccount.addContributionPoint(servicePoint);
                accountRepository.save(authorAccount);
                accountRepository.save(userAccount);

                savePoint(servicePoint, authorAccount, userAccount, service);
            }
        }

        return updatedService;

    }

    // 1. Check if the service's status is "Approved".
    // 2. Compare the service's start and end date to current date.
    // 3. Check if the service still has room to use.
    private String validateUseService(ServiceEntity service, AccountEntity userAccount, long currentDateInMillis) throws Exception {
        String errorMsg = "";
        if (!service.getStatus().getName().equals("Ongoing")) {
            errorMsg += "This service has not started yet;";
        }
        if (service.getStartDate().getTime() > currentDateInMillis
                || service.getEndDate().getTime() < currentDateInMillis) {
            errorMsg += "You cannot use this service at this time;";
        }
        int remainingSpots = serviceRepository.getUsedSpot(service.getId());
        if (remainingSpots == service.getQuota()) {
            errorMsg += "This service is full;";
        }
        if (userAccount.getBalancePoint() < service.getPoint()) {
            errorMsg += "This account does not have enough point to use the service;";
        }
        return errorMsg;
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

        PointEntity receiverPoint = new PointEntity();
        receiverPoint.setAmount(amount);
        receiverPoint.setAccount(authorAccount);
        receiverPoint.setCreatedDate(currentTimestamp);
        receiverPoint.setIsReceived(true);
        receiverPoint.setDescription("Account " + authorAccount.getEmail() +
                " received point for providing service: " + serviceEntity.getId());

        pointRepository.save(senderPoint);
        pointRepository.save(receiverPoint);
    }

    private List<ServiceResponse> convertEntitiesToResponses(List<ServiceEntity> serviceEntityList)
            throws Exception {
        List<ServiceResponse> result = ServiceResponse.convertToResponseList(serviceEntityList);
        for (ServiceResponse response : result) {
            int remainingSpot = serviceRepository.getRemainingSpot(response.getId());
            response.setSpot(remainingSpot);
        }
        return result;
    }

    private Map<String, Object> getServiceResponseMap(Page<ServiceEntity> pageServices) throws Exception {
        List<ServiceEntity> serviceEntityList = pageServices.getContent();
        List<ServiceResponse> serviceResponses = convertEntitiesToResponses(serviceEntityList);

        Map<String, Object> response = new HashMap<>();
        response.put("services", serviceResponses);
        response.put("currentPage", pageServices.getNumber());
        response.put("totalItems", pageServices.getTotalElements());
        response.put("totalPages", pageServices.getTotalPages());

        return response;
    }
}
