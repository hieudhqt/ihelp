package com.swp.ihelp.scheduler;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.account.AccountRepository;
import com.swp.ihelp.app.eventjointable.EventHasAccountRepository;
import com.swp.ihelp.app.status.AccountStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
public class AccountScheduler {
    static Logger logger = LoggerFactory.getLogger(EventScheduler.class);

    @Qualifier("accountTaskScheduler")
    private final TaskScheduler scheduler;

    private UnbanTask unbanTask;

    private AccountRepository accountRepository;
    private EventHasAccountRepository eventHasAccountRepository;

    @Value("${date.ban-date}")
    private int banDays;

    @Autowired
    public AccountScheduler(TaskScheduler scheduler, UnbanTask unbanTask, AccountRepository accountRepository, EventHasAccountRepository eventHasAccountRepository) {
        this.scheduler = scheduler;
        this.unbanTask = unbanTask;
        this.accountRepository = accountRepository;
        this.eventHasAccountRepository = eventHasAccountRepository;
    }

    @Scheduled(cron = "4 0 0 * * *")
    @Transactional
    public void autoBanAccount() {
        try {
            logger.info("Auto ban account");
            LocalDate currentDate = LocalDate.now();
            String startDate = "", endDate = "";
            switch (currentDate.getMonthValue()) {
                case 1:
                case 2:
                case 3:
                    startDate = currentDate.getYear() + "-01-01";
                    endDate = currentDate.getYear() + "-03-31";
                    break;
                case 4:
                case 5:
                case 6:
                    startDate = currentDate.getYear() + "-04-01";
                    endDate = currentDate.getYear() + "-06-30";
                    break;
                case 7:
                case 8:
                case 9:
                    startDate = currentDate.getYear() + "-07-01";
                    endDate = currentDate.getYear() + "-09-30";
                    break;
                case 10:
                case 11:
                case 12:
                    startDate = currentDate.getYear() + "-10-01";
                    endDate = currentDate.getYear() + "-12-31";
                    break;
            }

            List<AccountEntity> userAccounts = accountRepository.findByRoleName("ROLE_USER");
            for (AccountEntity account : userAccounts) {
                int negativeRatingCount = eventHasAccountRepository.getRatingCountByJoinDate(account.getEmail(),
                        1, startDate, endDate);
                if (negativeRatingCount >= 3) {
                    accountRepository.updateStatus(account.getEmail(), AccountStatusEnum.SUSPENDED.getId());
                    logger.info("Account " + account.getEmail() + " is banned for " + banDays + " days due to negative ratings.");
                    unbanTask.setEmail(account.getEmail());
                    scheduler.schedule(unbanTask, Date.from(LocalDateTime.now().plusDays(banDays)
                            .atZone(ZoneId.systemDefault()).toInstant()));
                }
            }
        } catch (Exception e) {
            logger.error("Error when auto banning accounts: " + e.getMessage());
        }

    }
}

@Component
class UnbanTask implements Runnable {
    Logger logger = LoggerFactory.getLogger(UnbanTask.class);

    private AccountRepository repository;
    private String email;

    @Autowired
    public void setRepository(AccountRepository repository) {
        this.repository = repository;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    @Transactional
    public void run() {
        try {
            logger.info("Unbanning account " + email);
            repository.updateStatus(email, AccountStatusEnum.ACTIVE.getId());
            logger.info("Account " + email + " unbanned.");
        } catch (Exception e) {
            logger.info("Error when unbanning " + email + ":" + e.getMessage());
        }
    }
}
