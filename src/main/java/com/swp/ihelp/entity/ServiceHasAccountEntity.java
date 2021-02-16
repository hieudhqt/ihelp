package com.swp.ihelp.entity;

import com.swp.ihelp.app.account.AccountEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "service_has_account", schema = "ihelp", catalog = "")
@IdClass(ServiceHasAccountEntityPK.class)
public class ServiceHasAccountEntity {
    private String serviceId;
    private String accountEmail;
    private ServiceEntity serviceByServiceId;
    private AccountEntity accountByAccountEmail;

    @Id
    @Column(name = "service_id", nullable = false, length = 20)
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Id
    @Column(name = "account_email", nullable = false, length = 320)
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
        ServiceHasAccountEntity that = (ServiceHasAccountEntity) o;
        return Objects.equals(serviceId, that.serviceId) &&
                Objects.equals(accountEmail, that.accountEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceId, accountEmail);
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
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false, insertable = false, updatable = false)
    public AccountEntity getAccountByAccountEmail() {
        return accountByAccountEmail;
    }

    public void setAccountByAccountEmail(AccountEntity accountByAccountEmail) {
        this.accountByAccountEmail = accountByAccountEmail;
    }
}
