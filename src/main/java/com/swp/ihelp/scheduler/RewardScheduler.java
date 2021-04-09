package com.swp.ihelp.scheduler;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.reward.RewardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class RewardScheduler {
    static Logger logger = LoggerFactory.getLogger(RewardScheduler.class);

    private RewardRepository rewardRepository;
    private AccountRepository accountRepository;

    @Value("${reward.balance-point-reward-percent}")
    private float pointRewardPercent;

    @Autowired
    public RewardScheduler(RewardRepository rewardRepository, AccountRepository accountRepository) {
        this.rewardRepository = rewardRepository;
        this.accountRepository = accountRepository;
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
            }
        } catch (Exception e) {
            logger.error("Error when auto rewarding: " + e.getMessage());
        }
    }
}
