package com.swp.ihelp.app.notification;

import com.swp.ihelp.app.account.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "device", schema = "ihelp")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DeviceEntity {

    @Id
    @Column(name = "token")
    private String token;

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    private AccountEntity account;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceEntity that = (DeviceEntity) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }
}
