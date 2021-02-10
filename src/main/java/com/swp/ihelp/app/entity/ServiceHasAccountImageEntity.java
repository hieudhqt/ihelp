package com.swp.ihelp.app.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "service_has_account_image", schema = "ihelp", catalog = "")
@IdClass(ServiceHasAccountImageEntityPK.class)
public class ServiceHasAccountImageEntity {
    private String serviceId;
    private String accountImageId;
    private ServiceEntity serviceByServiceId;
    private AccountImageEntity accountImageByAccountImageId;

    @Id
    @Column(name = "service_id", nullable = false, length = 20)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "account_image_id", nullable = false, length = 45)
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
        ServiceHasAccountImageEntity that = (ServiceHasAccountImageEntity) o;
        return Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(accountImageId, that.accountImageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, accountImageId);
    }

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    public ServiceEntity getServiceByServiceId() {
        return serviceByServiceId;
    }

    public void setServiceByServiceId(ServiceEntity serviceByServiceId) {
        this.serviceByServiceId = serviceByServiceId;
    }

    @ManyToOne
    @JoinColumn(name = "account_image_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    public AccountImageEntity getAccountImageByAccountImageId() {
        return accountImageByAccountImageId;
    }

    public void setAccountImageByAccountImageId(AccountImageEntity accountImageByAccountImageId) {
        this.accountImageByAccountImageId = accountImageByAccountImageId;
    }
}
