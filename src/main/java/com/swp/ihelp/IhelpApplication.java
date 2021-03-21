package com.swp.ihelp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IhelpApplication {

    public static void main(String[] args) {
        SpringApplication.run(IhelpApplication.class, args);
    }

}
