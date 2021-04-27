package com.swp.ihelp.scheduler;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.notification.DeviceRepository;
import com.swp.ihelp.app.notification.NotificationEntity;
import com.swp.ihelp.app.notification.NotificationRepository;
import com.swp.ihelp.app.reward.RewardRepository;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class RewardScheduler {
    static Logger logger = LoggerFactory.getLogger(RewardScheduler.class);

    private RewardRepository rewardRepository;
    private AccountRepository accountRepository;

    //Use notification repository due to not using @Repository and @Service in the same class
    private DeviceRepository deviceRepository;
    private NotificationRepository notificationRepository;

    //This service is only related to Firebase Cloud Messaging. It does not integrate with DAO layer or vice versa
    private PushNotificationService pushNotificationService;

    @Value("${reward.balance-point-reward-percent}")
    private float pointRewardPercent;

    public RewardScheduler(RewardRepository rewardRepository, AccountRepository accountRepository, DeviceRepository deviceRepository, NotificationRepository notificationRepository, PushNotificationService pushNotificationService) {
        this.rewardRepository = rewardRepository;
        this.accountRepository = accountRepository;
        this.deviceRepository = deviceRepository;
        this.notificationRepository = notificationRepository;
        this.pushNotificationService = pushNotificationService;
    }

    @Scheduled(cron = "2 0 0 1 1/3 ?")
    @Transactional
    public void rewardBalancePoint() {
        try {
            logger.info("Auto reward");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate localDate = LocalDate.now();

            String currentDate = localDate.minusDays(1).format(formatter);
            String startDate = localDate.minusMonths(3).format(formatter);

            List<AccountEntity> accountEntities = accountRepository
                    .getTop100AccountsByContributionAndDate(startDate, currentDate);
            for (AccountEntity account : accountEntities) {
                int totalPointInPeriod = rewardRepository.getTotalPointByDate(
                        account.getEmail(), startDate, currentDate);

                int pointToReward = Math.round(totalPointInPeriod * pointRewardPercent);
                account.addBalancePoint(pointToReward);
                accountRepository.save(account);

                List<String> hostDeviceTokens = deviceRepository.findByEmail(account.getEmail());

                if (hostDeviceTokens != null && !hostDeviceTokens.isEmpty()) {
                    PushNotificationRequest notificationRequest = new PushNotificationRequest()
                            .setTitle("You are rewarded: " + pointToReward + " from the system")
                            .setMessage("Due to having actively volunteer contribution, you have been rewarded: " + pointToReward)
                            .setRegistrationTokens(hostDeviceTokens);

                    pushNotificationService.sendPushNotificationToMultiDevices(notificationRequest);
                }

                notificationRepository.save(new NotificationEntity()
                        .setTitle("You are rewarded: " + pointToReward + " from the system")
                        .setMessage("Due to having actively volunteer contribution, you have been rewarded: " + pointToReward)
                        .setDate(new Timestamp(System.currentTimeMillis()))
                        .setAccountEntity(new AccountEntity().setEmail(account.getEmail())));
            }
        } catch (Exception e) {
            logger.error("Error when auto rewarding: " + e.getMessage());
        }
    }
}
