package com.swp.ihelp.app.entity;

import com.swp.ihelp.app.account.AccountEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "event_has_account", schema = "ihelp", catalog = "")
@IdClass(EventHasAccountEntityPK.class)
public class EventHasAccountEntity {
    private String eventId;
    private String accountEmail;
    private EventEntity eventByEventId;
    private AccountEntity accountByAccountEmail;

    @Id
    @Column(name = "event_id", nullable = false, length = 20)
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Id
    @Column(name = "account_email", nullable = false, length = 320)
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
        EventHasAccountEntity that = (EventHasAccountEntity) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(accountEmail, that.accountEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, accountEmail);
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
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false, insertable = false, updatable = false)
    public AccountEntity getAccountByAccountEmail() {
        return accountByAccountEmail;
    }

    public void setAccountByAccountEmail(AccountEntity accountByAccountEmail) {
        this.accountByAccountEmail = accountByAccountEmail;
    }
}
