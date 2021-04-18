package com.swp.ihelp.app.service;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.notification.NotificationEntity;
import com.swp.ihelp.app.notification.NotificationService;
import com.swp.ihelp.app.service.request.CreateServiceRequest;
import com.swp.ihelp.app.service.request.RejectServiceRequest;
import com.swp.ihelp.app.service.request.UpdateServiceRequest;
import com.swp.ihelp.app.service.response.ServiceDetailResponse;
import com.swp.ihelp.google.firebase.fcm.PushNotificationRequest;
import com.swp.ihelp.google.firebase.fcm.PushNotificationService;
import com.swp.ihelp.message.ServiceMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ServiceController {

    private ServiceVolunteerService serviceVolunteerService;

    private ServiceMessage serviceMessage;

    private NotificationService notificationService;

    private PushNotificationService pushNotificationService;

    @Autowired
    public ServiceController(ServiceVolunteerService serviceVolunteerService, ServiceMessage serviceMessage, NotificationService notificationService, PushNotificationService pushNotificationService) {
        this.serviceVolunteerService = serviceVolunteerService;
        this.serviceMessage = serviceMessage;
        this.notificationService = notificationService;
        this.pushNotificationService = pushNotificationService;
    }

    @GetMapping("/services")
    public ResponseEntity<Map<String, Object>> findAll(@RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = serviceVolunteerService.findAll(page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/services/filter")
    public ResponseEntity<Map<String, Object>> findAllWithFilter(@RequestParam(value = "search", required = false)
                                                                         String search,
                                                                 @RequestParam(value = "page") int page)
            throws Exception {
        Map<String, Object> response = serviceVolunteerService.findAll(page, search);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/services/{serviceId}")
    public ServiceDetailResponse findById(@PathVariable String serviceId) throws Exception {
        return serviceVolunteerService.findById(serviceId);
    }

    @GetMapping("/services/title/{serviceTitle}")
    public ResponseEntity<Map<String, Object>> findByTitle(@PathVariable String serviceTitle,
                                                           @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = serviceVolunteerService.findByTitle(serviceTitle, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @GetMapping("/services/type/{typeId}")
//    public ResponseEntity<Map<String, Object>> findByServiceTypeId(@PathVariable int typeId,
//                                                                   @RequestParam(value = "page") int page) throws Exception {
//        Map<String, Object> response = serviceVolunteerService.findByServiceTypeId(typeId, page);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @GetMapping("/services/status/{statusId}")
    public ResponseEntity<Map<String, Object>> findByStatusId(@PathVariable int statusId,
                                                              @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = serviceVolunteerService.findByStatusId(statusId, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/services/account/{email}")
    public ResponseEntity<Map<String, Object>> findByAuthorEmail(@PathVariable String email,
                                                                 @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = serviceVolunteerService.findByAuthorEmail(email, page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/services/nearby/{radius}")
    public ResponseEntity<Map<String, Object>> findNearbyEvents(@PathVariable float radius,
                                                                @RequestParam(value = "lat") double lat,
                                                                @RequestParam(value = "lng") double lng,
                                                                @RequestParam(value = "page") int page) throws Exception {
        Map<String, Object> response = serviceVolunteerService.findNearbyServices(page, radius, lat, lng);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/services")
    public ResponseEntity<String> addService(@Valid @RequestBody CreateServiceRequest createServiceRequest) throws Exception {
        String serviceId = serviceVolunteerService.insert(createServiceRequest);
        return ResponseEntity.ok(serviceMessage.getServiceAddedMessage(serviceId));
    }

    @PutMapping("/services")
    public ResponseEntity<ServiceDetailResponse> updateService(@RequestBody UpdateServiceRequest request) throws Exception {
        ServiceDetailResponse updatedService = serviceVolunteerService.update(request);
        return new ResponseEntity<>(updatedService, HttpStatus.OK);
    }

    @PutMapping("/services/{email}/approve/{serviceId}")
    public ResponseEntity<String> approve(@PathVariable String email,
                                          @PathVariable String serviceId) throws Exception {
        ServiceEntity approvedService = serviceVolunteerService.approve(serviceId, email);

        List<String> deviceTokens = notificationService.findDeviceTokensByEmail(approvedService.getAuthorAccount().getEmail());
        if (deviceTokens != null || !deviceTokens.isEmpty()) {
            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest()
                    .setTitle("Your service: \"" + approvedService.getTitle() + "\" has been approved")
                    .setMessage("Service \"" + approvedService.getTitle() + " is now able to be used")
                    .setRegistrationTokens(deviceTokens);
            pushNotificationService.sendPushNotificationToMultiDevices(pushNotificationRequest);

            notificationService.insert(new NotificationEntity()
                    .setTitle("Your service: \"" + approvedService.getTitle() + "\" has been approved")
                    .setMessage("Service \"" + approvedService.getTitle() + " is now able to be used")
                    .setDate(new Timestamp(System.currentTimeMillis()))
                    .setAccountEntity(new AccountEntity().setEmail(approvedService.getAuthorAccount().getEmail())));
        } else {
//            throw new EntityNotFoundException("Account: " + email + " has no device token.");
        }

        return ResponseEntity.ok("Service " + serviceId + " has been approved by " + email);
    }

    @PutMapping("/services/reject")
    public ResponseEntity<String> reject(@Valid @RequestBody RejectServiceRequest request) throws Exception {
        ServiceEntity rejectedService = serviceVolunteerService.reject(request);

        List<String> deviceTokens = notificationService.findDeviceTokensByEmail(rejectedService.getAuthorAccount().getEmail());
        if (deviceTokens != null || !deviceTokens.isEmpty()) {
            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest()
                    .setTitle("Your service: \"" + rejectedService.getTitle() + "\" has been rejected")
                    .setMessage("Service \"" + rejectedService.getTitle() + " has been rejected with following reason: " + request.getReason())
                    .setRegistrationTokens(deviceTokens);
            pushNotificationService.sendPushNotificationToMultiDevices(pushNotificationRequest);

            notificationService.insert(new NotificationEntity()
                    .setTitle("Your service: \"" + rejectedService.getTitle() + "\" has been rejected")
                    .setMessage("Service \"" + rejectedService.getTitle() + " has been rejected with following reason: " + request.getReason())
                    .setDate(new Timestamp(System.currentTimeMillis()))
                    .setAccountEntity(new AccountEntity().setEmail(rejectedService.getAuthorAccount().getEmail())));
        } else {
//            throw new EntityNotFoundException("Account: " + email + " has no device token.");
        }

        return ResponseEntity.ok("Service " + request.getServiceId() +
                " has been rejected by " + request.getManagerEmail() + ", reason: "
                + request.getReason());
    }

    @DeleteMapping("/services/{serviceId}")
    public String deleteService(@PathVariable String serviceId) throws Exception {
        ServiceDetailResponse response = serviceVolunteerService.findById(serviceId);
        if (response == null) {
            throw new RuntimeException(serviceMessage.getServiceNotFoundMessage() + serviceId);
        }
        serviceVolunteerService.deleteById(serviceId);
        return serviceMessage.getServiceDeletedMessage(serviceId);
    }

    @PostMapping("/services/{email}/{serviceId}")
    public String useService(@PathVariable String email, @PathVariable String serviceId) throws Exception {
        ServiceEntity usedService = serviceVolunteerService.useService(email, serviceId);

        List<String> deviceTokens = notificationService.findDeviceTokensByEmail(usedService.getAuthorAccount().getEmail());
        if (deviceTokens != null || !deviceTokens.isEmpty()) {
            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest()
                    .setTitle(email + " use event \"" + usedService.getTitle())
                    .setMessage("Service \"" + usedService.getTitle() + " has been used by: " + email)
                    .setRegistrationTokens(deviceTokens);
            pushNotificationService.sendPushNotificationToMultiDevices(pushNotificationRequest);

            notificationService.insert(new NotificationEntity()
                    .setTitle(email + " use event \"" + usedService.getTitle())
                    .setMessage("Service \"" + usedService.getTitle() + " has been used by: " + email)
                    .setDate(new Timestamp(System.currentTimeMillis()))
                    .setAccountEntity(new AccountEntity().setEmail(usedService.getAuthorAccount().getEmail())));
        } else {
//            throw new EntityNotFoundException("Account: " + email + " has no device token.");
        }

        return serviceMessage.getServiceUsedMessage(email, serviceId);
    }

}
