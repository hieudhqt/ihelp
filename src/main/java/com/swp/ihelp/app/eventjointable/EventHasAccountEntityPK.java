package com.swp.ihelp.app.eventjointable;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventHasAccountEntityPK implements Serializable {
    private String eventId;
    private String accountEmail;

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
