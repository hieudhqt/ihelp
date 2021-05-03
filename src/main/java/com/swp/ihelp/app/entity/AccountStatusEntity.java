package com.swp.ihelp.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "account_status", schema = "ihelp", catalog = "")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AccountStatusEntity {

    @Id
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Basic
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountStatusEntity that = (AccountStatusEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
