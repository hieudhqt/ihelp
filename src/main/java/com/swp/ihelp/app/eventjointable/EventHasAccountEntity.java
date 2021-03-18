package com.swp.ihelp.app.eventjointable;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.event.EventEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "event_has_account", schema = "ihelp")
@NoArgsConstructor
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

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        EventHasAccountEntity that = (EventHasAccountEntity) o;
//        return Objects.equals(event, that.event) && Objects.equals(account, that.account);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(event, account);
//    }
}
