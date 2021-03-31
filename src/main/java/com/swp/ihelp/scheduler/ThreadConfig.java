package com.swp.ihelp.scheduler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

//@Configuration
@EnableAsync
public class ThreadConfig {

    @Bean(name = "eventTaskExecutor")
    public TaskExecutor threadPoolTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("default_task_executor_thread");
        executor.initialize();

        return executor;
    }

    @Bean
    public CommandLineRunner schedulingRunner(@Qualifier("eventTaskExecutor") TaskExecutor executor) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                executor.execute(new EventTaskDispatcher());
            }
        };
    }
}
