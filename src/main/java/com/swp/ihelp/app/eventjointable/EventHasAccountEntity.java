package com.swp.ihelp.app.eventjointable;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.event.EventEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "event_has_account", schema = "ihelp")
@AllArgsConstructor
@Getter
public class EventHasAccountEntity {

    @EmbeddedId
    private EventHasAccountEntityPK id = new EventHasAccountEntityPK();

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private EventEntity event;

    public void setEvent(EventEntity event) {
        if (this.event != null) {
            this.event.internalRemoveEventAccount(this);
        }
        this.event = event;
        if (event != null) {
            event.internalAddEventAccount(this);
        }
    }

    @ManyToOne
    @MapsId("accountEmail")
    @JoinColumn(name = "account_email")
    private AccountEntity account;

    public void setAccount(AccountEntity accountEntity) {
        this.account = accountEntity;
    }

    @Column(name = "join_date")
    private Timestamp joinDate = new Timestamp(System.currentTimeMillis());

    @Column(name = "is_evaluated", columnDefinition = "BIT(1) default b'0'")
    private Boolean isEvaluated;

    @Column(name = "rating")
    private Short rating;

    public EventHasAccountEntity() {
        this.isEvaluated = false;
    }

    public void setEvaluated(Boolean evaluated) {
        isEvaluated = evaluated;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }
}
