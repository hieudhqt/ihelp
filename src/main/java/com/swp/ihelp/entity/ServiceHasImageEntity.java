package com.swp.ihelp.entity;

import javax.persistence.*;
import java.util.Objects;
import com.swp.ihelp.app.service.ServiceEntity;

@Entity
@Table(name = "service_has_image", schema = "ihelp", catalog = "")
@IdClass(ServiceHasImageEntityPK.class)
public class ServiceHasImageEntity {
    private String serviceId;
    private String accountImageId;
    private ServiceEntity serviceByServiceId;
    private ImageEntity accountImageByAccountImageId;

    @Id
    @Column(name = "service_id", nullable = false, length = 20)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "image_id", nullable = false, length = 45)
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
        ServiceHasImageEntity that = (ServiceHasImageEntity) o;
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
    @JoinColumn(name = "image_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    public ImageEntity getAccountImageByAccountImageId() {
        return accountImageByAccountImageId;
    }

    public void setAccountImageByAccountImageId(ImageEntity accountImageByAccountImageId) {
        this.accountImageByAccountImageId = accountImageByAccountImageId;
    }
}
