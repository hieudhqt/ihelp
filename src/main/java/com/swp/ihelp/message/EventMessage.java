package com.swp.ihelp.message;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@PropertySource("classpath:message.properties")
@Component
public class EventMessage {
    @Value("${message.event.event-not-found}")
    private String eventNotFoundMessage;
    @Value("${message.event.event-added}")
    private String eventAddedMessage;
    @Value("${message.event.event-deleted}")
    private String eventDeletedMessage;
    @Value("${message.event.event-updated}")
    private String eventUpdatedMessage;
    @Value("${message.event.event-join}")
    private String eventJoinedMessage;

    @Value("${message.event.event-quited}")
    private String eventQuitedMessage;

    @Value("${message.event.event-unavailable}")
    private String eventUnavailableMessage;

    public String getEventJoinedMessage(String eventId, String email) {
        String result = this.eventJoinedMessage.replaceFirst("#", email);
        result = result.replaceFirst("#", eventId);
        return result;
    }

    public String getEventUpdatedMessage(String eventId) {
        String result = this.eventUpdatedMessage.replaceFirst("#", eventId);
        return result;
    }

    public String getEventAddedMessage(String eventId) {
        String result = this.eventAddedMessage.replaceFirst("#", eventId);
        return result;
    }

    public String getEventQuitedMessage(String email, String eventId) {
        String result = this.eventQuitedMessage.replaceFirst("#", email);
        result = result.replaceFirst("#", eventId);
        return result;
    }
}
