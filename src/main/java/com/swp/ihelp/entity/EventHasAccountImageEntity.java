package com.swp.ihelp.entity;

import com.swp.ihelp.app.event.EventEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "event_has_account_image", schema = "ihelp", catalog = "")
@IdClass(EventHasAccountImageEntityPK.class)
public class EventHasAccountImageEntity {
    private String eventId;
    private String accountImageId;
    private EventEntity eventByEventId;
    private AccountImageEntity accountImageByAccountImageId;

    @Id
    @Column(name = "event_id", nullable = false, length = 20)
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Id
    @Column(name = "account_image_id", nullable = false, length = 45)
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
        EventHasAccountImageEntity that = (EventHasAccountImageEntity) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(accountImageId, that.accountImageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, accountImageId);
    }

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    public EventEntity getEventByEventId() {
        return eventByEventId;
    }

    public void setEventByEventId(EventEntity eventByEventId) {
        this.eventByEventId = eventByEventId;
    }

    @ManyToOne
    @JoinColumn(name = "account_image_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    public AccountImageEntity getAccountImageByAccountImageId() {
        return accountImageByAccountImageId;
    }

    public void setAccountImageByAccountImageId(AccountImageEntity accountImageByAccountImageId) {
        this.accountImageByAccountImageId = accountImageByAccountImageId;
    }
}
