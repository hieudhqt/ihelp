package com.swp.ihelp.app.eventjointable;

import com.swp.ihelp.entity.AccountImageEntity;
import com.swp.ihelp.app.event.EventEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "event_has_account_image", schema = "ihelp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventHasAccountImageEntity {
    @EmbeddedId
    private EventHasAccountImageEntityPK id = new EventHasAccountImageEntityPK();

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne
    @MapsId("accountImageId")
    @JoinColumn(name = "account_image_id")
    private AccountImageEntity accountImage;
}
