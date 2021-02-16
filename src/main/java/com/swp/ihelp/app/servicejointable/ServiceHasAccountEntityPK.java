package com.swp.ihelp.app.servicejointable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ServiceHasAccountEntityPK implements Serializable {
    private String serviceId;
    private String accountEmail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceHasAccountEntityPK that = (ServiceHasAccountEntityPK) o;
        return Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(accountEmail, that.accountEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, accountEmail);
    }
}
