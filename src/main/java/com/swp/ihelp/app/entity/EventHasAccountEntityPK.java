package com.swp.ihelp.app.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class EventHasAccountEntityPK implements Serializable {
    private String eventId;
    private String accountEmail;

    @Column(name = "event_id", nullable = false, length = 20)
    @Id
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Column(name = "account_email", nullable = false, length = 320)
    @Id
    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventHasAccountEntityPK that = (EventHasAccountEntityPK) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(accountEmail, that.accountEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, accountEmail);
    }
}
