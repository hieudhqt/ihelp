package com.swp.ihelp.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@PropertySource("classpath:message.properties")
public class ServiceMessage implements Serializable {
    @Value("message.service.service-added")
    public String serviceNotFound;
}
