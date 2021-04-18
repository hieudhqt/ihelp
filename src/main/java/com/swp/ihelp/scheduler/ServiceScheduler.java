package com.swp.ihelp.scheduler;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.notification.DeviceRepository;
import com.swp.ihelp.app.notification.NotificationEntity;
import com.swp.ihelp.app.notification.NotificationRepository;
import com.swp.ihelp.app.reward.RewardEntity;
import com.swp.ihelp.app.reward.RewardRepository;
import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.service.ServiceRepository;
import com.swp.ihelp.app.status.StatusEntity;
import com.swp.ihelp.app.status.StatusEnum;
import com.swp.ihelp.google.firebase.fcm.PushNotificationRequest;
import com.swp.ihelp.google.firebase.fcm.PushNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class ServiceScheduler {
    static Logger logger = LoggerFactory.getLogger(ServiceScheduler.class);

    private ServiceRepository serviceRepository;
    private RewardRepository rewardRepository;
    private AccountRepository accountRepository;

    //Use notification repository due to not using @Repository and @Service in the same class
    private DeviceRepository deviceRepository;
    private NotificationRepository notificationRepository;

    //This service is only related to Firebase Cloud Messaging. It does not integrate with DAO layer or vice versa
    private PushNotificationService pushNotificationService;

    @Value("${date.max-days-to-approve}")
    private int maxDaysToApprove;

    @Value("${point.service-contribution-point}")
    private int serviceContributionPoint;

    @Autowired
    public ServiceScheduler(ServiceRepository serviceRepository, RewardRepository rewardRepository, AccountRepository accountRepository, DeviceRepository deviceRepository, NotificationRepository notificationRepository, PushNotificationService pushNotificationService) {
        this.serviceRepository = serviceRepository;
        this.rewardRepository = rewardRepository;
        this.accountRepository = accountRepository;
        this.deviceRepository = deviceRepository;
        this.notificationRepository = notificationRepository;
        this.pushNotificationService = pushNotificationService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoStartService() {
        try {
            logger.info("Auto start service");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());

            List<String> serviceIds
                    = serviceRepository.getServiceIdsToStartByDate(currentDate, StatusEnum.APPROVED.getId());
            for (String serviceId : serviceIds) {
                serviceRepository.updateStatus(serviceId, StatusEnum.ONGOING.getId());
            }
        } catch (Exception e) {
            logger.error("Error when auto starting services: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoCompleteService() {
        try {
            logger.info("Auto complete service");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());

            List<String> serviceIds
                    = serviceRepository.getServiceIdsToCompleteByDate(currentDate, StatusEnum.ONGOING.getId());
            for (String serviceId : serviceIds) {
                serviceRepository.updateStatus(serviceId, StatusEnum.COMPLETED.getId());

                ServiceEntity serviceEntity = serviceRepository.getOne(serviceId);

                RewardEntity reward = new RewardEntity();
                reward.setTitle("Reward for providing service " + serviceEntity.getTitle());
                reward.setDescription("");
                reward.setPoint(serviceContributionPoint);
                reward.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                reward.setAccount(serviceEntity.getAuthorAccount());
                rewardRepository.save(reward);

                accountRepository.updateContributionPoint
                        (serviceEntity.getAuthorAccount().getEmail(), serviceContributionPoint);

                List<String> deviceTokens = deviceRepository.findByEmail(serviceEntity.getAuthorAccount().getEmail());

                PushNotificationRequest notificationRequest = new PushNotificationRequest()
                        .setTitle("Your service: \"" + serviceEntity.getTitle() + "\" has ended")
                        .setMessage("Service \"" + serviceEntity.getTitle() + "\" has ended")
                        .setRegistrationTokens(deviceTokens);

                pushNotificationService.sendPushNotificationToMultiDevices(notificationRequest);

                notificationRepository.save(new NotificationEntity()
                        .setTitle("Your service: \"" + serviceEntity.getTitle() + "\" has ended")
                        .setMessage("Service \"" + serviceEntity.getTitle() + "\" has ended")
                        .setDate(new Timestamp(System.currentTimeMillis()))
                        .setAccountEntity(new AccountEntity().setEmail(serviceEntity.getAuthorAccount().getEmail())));

            }
        } catch (Exception e) {
            logger.error("Error when auto completing services: " + e.getMessage());
        }
    }

    @Scheduled(cron = "1 0 0 * * *")
    @Transactional
    public void autoRejectService() {
        try {
            logger.info("Auto reject service");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());

            List<String> serviceIds
                    = serviceRepository.getExpiredServiceIds(currentDate, StatusEnum.PENDING.getId(), maxDaysToApprove);
            for (String serviceId : serviceIds) {
                ServiceEntity serviceToReject = serviceRepository.getOne(serviceId);
                serviceToReject.setReason("This service has passed approval due date.");
                serviceToReject.setStatus(new StatusEntity().setId(StatusEnum.REJECTED.getId()));
                serviceRepository.save(serviceToReject);

                //Push notification to service's host
                List<String> hostDeviceTokens = deviceRepository.findByEmail(serviceToReject.getAuthorAccount().getEmail());

                PushNotificationRequest notificationRequest = new PushNotificationRequest()
                        .setTitle("Your service: \"" + serviceToReject.getTitle() + "\" has been rejected because approval deadline was exceeded")
                        .setMessage("Service \"" + serviceToReject.getTitle() + "\" has been rejected, please contact admin or manager for more information")
                        .setRegistrationTokens(hostDeviceTokens);

                pushNotificationService.sendPushNotificationToMultiDevices(notificationRequest);

                notificationRepository.save(new NotificationEntity()
                        .setTitle("Your service: \"" + serviceToReject.getTitle() + "\" has been rejected because approval deadline was exceeded")
                        .setMessage("Service \"" + serviceToReject.getTitle() + "\" has been rejected, please contact admin or manager for more information")
                        .setDate(new Timestamp(System.currentTimeMillis()))
                        .setAccountEntity(new AccountEntity().setEmail(serviceToReject.getAuthorAccount().getEmail())));
            }
        } catch (Exception e) {
            logger.error("Error when auto rejecting services: " + e.getMessage());
        }
    }
}
