package com.swp.ihelp.app.servicejointable;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.service.ServiceEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "service_has_account", schema = "ihelp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceHasAccountEntity {
    @EmbeddedId
    private ServiceHasAccountEntityPK id = new ServiceHasAccountEntityPK();

    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private ServiceEntity service;

    @ManyToOne
    @MapsId("accountEmail")
    @JoinColumn(name = "account_email")
    private AccountEntity account;

    @Column(name = "use_date")
    private long useDate = new Date().getTime();
}
