package com.swp.ihelp.app.entity;

import com.swp.ihelp.app.account.AccountEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "point", schema = "ihelp", catalog = "")
@Data
public class PointEntity {

    @Id
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Basic
    @Column(name = "amount", nullable = true)
    private int amount;

    @Basic
    @Column(name = "description", nullable = true, length = 1000)
    private String description;

    @Basic
    @Column(name = "is_received", nullable = true)
    private Boolean isReceived;

    @Basic
    @Column(name = "created_date", nullable = true)
    private long createdDate;

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    private AccountEntity accountByAccountEmail;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private EventEntity eventByEventId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointEntity that = (PointEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(description, that.description) &&
                Objects.equals(isReceived, that.isReceived) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, description, isReceived, createdDate);
    }

}
