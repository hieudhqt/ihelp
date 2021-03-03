package com.swp.ihelp.message;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@PropertySource("classpath:message.properties")
@Getter
public class ServiceMessage implements Serializable {
    @Value("${message.service.service-not-found}")
    private String serviceNotFound;

    @Value("${message.service.service-added}")
    private String serviceAdded;

    @Value("${message.service.service-deleted}")
    private String serviceDeleted;
}
