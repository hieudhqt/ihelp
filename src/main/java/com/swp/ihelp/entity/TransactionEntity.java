package com.swp.ihelp.entity;

import com.swp.ihelp.app.account.AccountEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "transaction", schema = "ihelp", catalog = "")
@Data
public class TransactionEntity {

    @Id
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Basic
    @Column(name = "point", nullable = false)
    private int point;

    @Basic
    @Column(name = "description", nullable = true, length = 200)
    private String description;

    @Basic
    @Column(name = "date", nullable = true)
    private long date;

    @Basic
    @Column(name = "type", nullable = true, length = 45)
    private String type;

    @ManyToOne
    @JoinColumn(name = "sender_email", referencedColumnName = "email", nullable = false)
    private AccountEntity accountBySenderEmail;

    @ManyToOne
    @JoinColumn(name = "receiver_email", referencedColumnName = "email", nullable = false)
    private AccountEntity accountByReceiverEmail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(point, that.point) &&
                Objects.equals(description, that.description) &&
                Objects.equals(date, that.date) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, point, description, date, type);
    }

}
