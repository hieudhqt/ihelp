package com.swp.ihelp.app.eventjointable;

import com.swp.ihelp.app.entity.AccountEntity;
import com.swp.ihelp.app.event.EventEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "event_has_account", schema = "ihelp")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventHasAccountEntity {

    @EmbeddedId
    private EventHasAccountEntityPK id = new EventHasAccountEntityPK();

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne
    @MapsId("accountEmail")
    @JoinColumn(name = "account_email")
    private AccountEntity account;

    @Column(name = "join_date")
    private long joinDate = new Date().getTime();

}
