package com.swp.ihelp.app.eventjointable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventHasAccountImageEntityPK implements Serializable {
    private String eventId;
    private String accountImageId;

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
