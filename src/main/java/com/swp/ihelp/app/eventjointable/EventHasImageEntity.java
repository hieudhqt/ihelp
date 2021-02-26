package com.swp.ihelp.app.eventjointable;

import com.swp.ihelp.app.entity.ImageEntity;
import com.swp.ihelp.app.event.EventEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "event_has_image", schema = "ihelp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventHasImageEntity {
    @EmbeddedId
    private EventHasImageEntityPK id = new EventHasImageEntityPK();

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne
    @MapsId("accountImageId")
    @JoinColumn(name = "image_id")
    private ImageEntity accountImage;
}
