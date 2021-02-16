package com.swp.ihelp.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class ServiceHasAccountImageEntityPK implements Serializable {
    private String serviceId;
    private String accountImageId;

    @Column(name = "service_id", nullable = false, length = 20)
    @Id
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "account_image_id", nullable = false, length = 45)
    @Id
    public String getAccountImageId() {
        return accountImageId;
    }

    public void setAccountImageId(String accountImageId) {
        this.accountImageId = accountImageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceHasAccountImageEntityPK that = (ServiceHasAccountImageEntityPK) o;
        return Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(accountImageId, that.accountImageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, accountImageId);
    }
}
