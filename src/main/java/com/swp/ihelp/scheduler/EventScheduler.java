package com.swp.ihelp.scheduler;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.event.EventRepository;
import com.swp.ihelp.app.notification.DeviceRepository;
import com.swp.ihelp.app.notification.NotificationEntity;
import com.swp.ihelp.app.notification.NotificationRepository;
import com.swp.ihelp.app.reward.RewardEntity;
import com.swp.ihelp.app.reward.RewardRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventScheduler {
    static Logger logger = LoggerFactory.getLogger(EventScheduler.class);

    private final EventRepository eventRepository;
    private final RewardRepository rewardRepository;
    private final AccountRepository accountRepository;

    //Use notification repository due to not using @Repository and @Service in the same class
    private final DeviceRepository deviceRepository;
    private final NotificationRepository notificationRepository;

    //This service is only related to Firebase Cloud Messaging. It does not integrate with DAO layer or vice versa
    private final PushNotificationService pushNotificationService;

    @Value("${point.event.host-event-bonus}")
    private float hostBonusPointPercent;

    @Value("${date.max-days-to-approve}")
    private int maxDaysToApprove;

    @Autowired
    public EventScheduler(EventRepository eventRepository, RewardRepository rewardRepository, AccountRepository accountRepository, DeviceRepository deviceRepository, NotificationRepository notificationRepository, PushNotificationService pushNotificationService) {
        this.eventRepository = eventRepository;
        this.rewardRepository = rewardRepository;
        this.accountRepository = accountRepository;
        this.deviceRepository = deviceRepository;
        this.notificationRepository = notificationRepository;
        this.pushNotificationService = pushNotificationService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoStartEvent() {
        try {
            logger.info("Auto start event");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());

            List<String> eventIds
                    = eventRepository.getEventIdsToStartByDate(currentDate, StatusEnum.APPROVED.getId());
            for (String eventId : eventIds) {
                eventRepository.updateStatus(eventId, StatusEnum.ONGOING.getId());
            }
        } catch (Exception e) {
            logger.error("Error when auto starting events: " + e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoCompleteEvent() {
        try {
            logger.info("Auto complete event");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());

            List<String> eventIds
                    = eventRepository.getEventIdsToCompleteByDate(currentDate, StatusEnum.ONGOING.getId());
            for (String eventId : eventIds) {
                eventRepository.updateStatus(eventId, StatusEnum.COMPLETED.getId());
                EventEntity eventEntity = eventRepository.getOne(eventId);

                int contributionPoint = Math.round(eventEntity.getPoint() * hostBonusPointPercent);

                RewardEntity reward = new RewardEntity();
                reward.setTitle("Reward for hosting event: " + eventEntity.getId());
                reward.setDescription("");
                reward.setPoint(contributionPoint);
                reward.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                reward.setAccount(eventEntity.getAuthorAccount());
                rewardRepository.save(reward);

                accountRepository.updateContributionPoint(eventEntity.getAuthorAccount().getEmail(),
                        contributionPoint);

                //Push notification to event's participant
                PushNotificationRequest participantsNotificationRequest = new PushNotificationRequest()
                        .setTitle("Event \"" + eventEntity.getTitle() + "\" is completed")
                        .setMessage("Event \"" + eventEntity.getTitle() + "\" is now being under host's evaluation")
                        .setTopic(eventId);

                pushNotificationService.sendPushNotificationToTopic(participantsNotificationRequest);

                //Push notification to event's host
                List<String> hostDeviceTokens = deviceRepository.findByEmail(eventEntity.getAuthorAccount().getEmail());

                if (hostDeviceTokens != null && !hostDeviceTokens.isEmpty()) {
                    Map<String, String> notificationData = new HashMap<>();
                    notificationData.put("evaluateRequiredEvents", eventId);

                    PushNotificationRequest hostNotificationRequest = new PushNotificationRequest()
                            .setTitle("Your event: \"" + eventEntity.getTitle() + "\" is completed")
                            .setMessage("Event \"" + eventEntity.getTitle() + "\" has participants in need of evaluating")
                            .setData(notificationData)
                            .setRegistrationTokens(hostDeviceTokens);

                    pushNotificationService.sendPushNotificationToMultiDevices(hostNotificationRequest);
                }

                notificationRepository.save(new NotificationEntity()
                        .setTitle("Your event: \"" + eventEntity.getTitle() + "\" is completed")
                        .setMessage("Event \"" + eventEntity.getTitle() + "\" has participants in need of evaluating")
                        .setDate(new Timestamp(System.currentTimeMillis()))
                        .setAccountEntity(new AccountEntity().setEmail(eventEntity.getAuthorAccount().getEmail())));

            }
        } catch (Exception e) {
            logger.error("Error when auto completing events: " + e.getMessage());
        }
    }

    @Scheduled(cron = "1 0 0 * * *")
    @Transactional
    public void autoRejectEvent() {
        try {
            logger.info("Auto reject event");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());

            List<String> eventIds
                    = eventRepository.getExpiredEventIds(currentDate, StatusEnum.PENDING.getId(), maxDaysToApprove);
            for (String eventId : eventIds) {
                EventEntity eventToReject = eventRepository.getOne(eventId);
                eventToReject.setReason("This event has passed approval due date.");
                eventToReject.setStatus(new StatusEntity().setId(StatusEnum.REJECTED.getId()));
                eventRepository.save(eventToReject);

                //Push notification to event's host
                List<String> hostDeviceTokens = deviceRepository.findByEmail(eventToReject.getAuthorAccount().getEmail());

                if (hostDeviceTokens != null && !hostDeviceTokens.isEmpty()) {
                    PushNotificationRequest hostNotificationRequest = new PushNotificationRequest()
                            .setTitle("Your event: \"" + eventToReject.getTitle() + "\" has been rejected because approval deadline was exceeded")
                            .setMessage("Event \"" + eventToReject.getTitle() + "\" has been rejected, please contact admin or manager for more information")
                            .setRegistrationTokens(hostDeviceTokens);

                    pushNotificationService.sendPushNotificationToMultiDevices(hostNotificationRequest);
                }

                notificationRepository.save(new NotificationEntity()
                        .setTitle("Your event: \"" + eventToReject.getTitle() + "\" has been rejected because approval deadline was exceeded")
                        .setMessage("Event \"" + eventToReject.getTitle() + "\" has been rejected, please contact admin or manager for more information")
                        .setDate(new Timestamp(System.currentTimeMillis()))
                        .setAccountEntity(new AccountEntity().setEmail(eventToReject.getAuthorAccount().getEmail())));

            }
        } catch (Exception e) {
            logger.error("Error when auto rejecting events: " + e.getMessage());
        }
    }

    @Scheduled(cron = "5 0 0 * * *")
    @Transactional
    public void autoDisableEvent() {
        try {
            logger.info("Auto disable event");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());

            List<String> eventIds
                    = eventRepository.getEmptyEventIds(currentDate);
            for (String eventId : eventIds) {
                EventEntity eventToDisable = eventRepository.getOne(eventId);
                eventToDisable.setReason("This event is disabled due to lacking participants on start date.");
                eventToDisable.setStatus(new StatusEntity().setId(StatusEnum.DISABLED.getId()));
                eventRepository.save(eventToDisable);

                int refundPoint = eventToDisable.getPoint() * eventToDisable.getQuota();
                accountRepository.updateBalancePoint(eventToDisable.getAuthorAccount().getEmail(), refundPoint);

                List<String> hostDeviceTokens = deviceRepository.findByEmail(eventToDisable.getAuthorAccount().getEmail());

                if (hostDeviceTokens != null && !hostDeviceTokens.isEmpty()) {
                    PushNotificationRequest hostNotificationRequest = new PushNotificationRequest()
                            .setTitle("Your event: \"" + eventToDisable.getTitle() + "\" has been disabled because event has started without participants")
                            .setMessage("Event \"" + eventToDisable.getTitle() + "\" has been disabled, please contact admin or manager for more information")
                            .setRegistrationTokens(hostDeviceTokens);

                    pushNotificationService.sendPushNotificationToMultiDevices(hostNotificationRequest);
                }

                notificationRepository.save(new NotificationEntity()
                        .setTitle("Your event: \"" + eventToDisable.getTitle() + "\" has been disabled because event has started without participants")
                        .setMessage("Event \"" + eventToDisable.getTitle() + "\" has been rejected, please contact admin or manager for more information")
                        .setDate(new Timestamp(System.currentTimeMillis()))
                        .setAccountEntity(new AccountEntity().setEmail(eventToDisable.getAuthorAccount().getEmail())));

            }
        } catch (Exception e) {
            logger.error("Error when auto disabling events: " + e.getMessage());
        }
    }

    @Scheduled(cron = "5 0 0 * * *")
    @Transactional
    public void autoEvaluateEvent() {
        try {
            logger.info("Auto disable event");

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = dateFormat.format(new Date());

            List<String> eventIds
                    = eventRepository.getEmptyEventIds(currentDate);
            for (String eventId : eventIds) {
                EventEntity eventToDisable = eventRepository.getOne(eventId);
                eventToDisable.setReason("This event is disabled due to lacking participants on start date.");
                eventToDisable.setStatus(new StatusEntity().setId(StatusEnum.DISABLED.getId()));
                eventRepository.save(eventToDisable);

                int refundPoint = eventToDisable.getPoint() * eventToDisable.getQuota();
                accountRepository.updateBalancePoint(eventToDisable.getAuthorAccount().getEmail(), refundPoint);
            }
        } catch (Exception e) {
            logger.error("Error when auto disabling events: " + e.getMessage());
        }
    }
}
