package com.swp.ihelp.scheduler;

import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.reward.RewardEntity;
import com.swp.ihelp.app.reward.RewardRepository;
import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.service.ServiceRepository;
import com.swp.ihelp.app.status.StatusEntity;
import com.swp.ihelp.app.status.StatusEnum;
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

    @Value("${date.max-days-to-approve}")
    private int maxDaysToApprove;

    @Value("${point.service-contribution-point}")
    private int serviceContributionPoint;

    @Autowired
    public ServiceScheduler(ServiceRepository serviceRepository, RewardRepository rewardRepository, AccountRepository accountRepository) {
        this.serviceRepository = serviceRepository;
        this.rewardRepository = rewardRepository;
        this.accountRepository = accountRepository;
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
                reward.setAccountByAccountEmail(serviceEntity.getAuthorAccount());
                rewardRepository.save(reward);

                accountRepository.updateContributionPoint
                        (serviceEntity.getAuthorAccount().getEmail(), serviceContributionPoint);

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
            }
        } catch (Exception e) {
            logger.error("Error when auto rejecting services: " + e.getMessage());
        }
    }
}
