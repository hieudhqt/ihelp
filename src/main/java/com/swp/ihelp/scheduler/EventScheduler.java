package com.swp.ihelp.scheduler;

import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.event.EventRepository;
import com.swp.ihelp.app.reward.RewardEntity;
import com.swp.ihelp.app.reward.RewardRepository;
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
public class EventScheduler {
    static Logger logger = LoggerFactory.getLogger(EventScheduler.class);

    private EventRepository eventRepository;
    private RewardRepository rewardRepository;
    private AccountRepository accountRepository;

    @Value("${point.event.host-event-bonus}")
    private float hostBonusPointPercent;

    @Value("${date.max-days-to-approve}")
    private int maxDaysToApprove;

    @Autowired
    public EventScheduler(EventRepository eventRepository, RewardRepository rewardRepository, AccountRepository accountRepository) {
        this.eventRepository = eventRepository;
        this.rewardRepository = rewardRepository;
        this.accountRepository = accountRepository;
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

                int contributionPoint = Math.round(eventEntity.getPoint() + eventEntity.getPoint() * hostBonusPointPercent);

                RewardEntity reward = new RewardEntity();
                reward.setTitle("Reward for hosting event: " + eventEntity.getTitle());
                reward.setDescription("");
                reward.setPoint(contributionPoint);
                reward.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                reward.setAccount(eventEntity.getAuthorAccount());
                rewardRepository.save(reward);

                accountRepository.updateContributionPoint(eventEntity.getAuthorAccount().getEmail(),
                        contributionPoint);
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
            }
        } catch (Exception e) {
            logger.error("Error when auto rejecting events: " + e.getMessage());
        }
    }
}
