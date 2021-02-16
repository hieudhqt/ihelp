package com.swp.ihelp.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class EventHasAccountImageEntityPK implements Serializable {
    private String eventId;
    private String accountImageId;

    @Column(name = "event_id", nullable = false, length = 20)
    @Id
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Column(name = "account_image_id", nullable = false, length = 45)
    @Id
    public String getAccountImageId() {
        return accountImageId;
    }

    public void setAccountImageId(String accountImageId) {
        this.accountImageId = accountImageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventHasAccountImageEntityPK that = (EventHasAccountImageEntityPK) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(accountImageId, that.accountImageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, accountImageId);
    }
}
