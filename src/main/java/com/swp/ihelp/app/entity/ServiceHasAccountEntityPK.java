package com.swp.ihelp.app.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class ServiceHasAccountEntityPK implements Serializable {
    private String serviceId;
    private String accountEmail;

    @Column(name = "service_id", nullable = false, length = 20)
    @Id
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "account_email", nullable = false, length = 320)
    @Id
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
        ServiceHasAccountEntityPK that = (ServiceHasAccountEntityPK) o;
        return Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(accountEmail, that.accountEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, accountEmail);
    }
}
