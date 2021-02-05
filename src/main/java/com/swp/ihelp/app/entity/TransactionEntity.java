package com.swp.ihelp.app.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "transaction", schema = "ihelp", catalog = "")
public class TransactionEntity {
    private String id;
    private Object point;
    private String description;
    private Object date;
    private String type;
    private AccountEntity accountBySenderEmail;
    private AccountEntity accountByReceiverEmail;

    @Id
    @Column(name = "id", nullable = false, length = 20)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "point", nullable = false)
    public Object getPoint() {
        return point;
    }

    public void setPoint(Object point) {
        this.point = point;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 200)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "date", nullable = true)
    public Object getDate() {
        return date;
    }

    public void setDate(Object date) {
        this.date = date;
    }

    @Basic
    @Column(name = "type", nullable = true, length = 45)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    @ManyToOne
    @JoinColumn(name = "sender_email", referencedColumnName = "email", nullable = false)
    public AccountEntity getAccountBySenderEmail() {
        return accountBySenderEmail;
    }

    public void setAccountBySenderEmail(AccountEntity accountBySenderEmail) {
        this.accountBySenderEmail = accountBySenderEmail;
    }

    @ManyToOne
    @JoinColumn(name = "receiver_email", referencedColumnName = "email", nullable = false)
    public AccountEntity getAccountByReceiverEmail() {
        return accountByReceiverEmail;
    }

    public void setAccountByReceiverEmail(AccountEntity accountByReceiverEmail) {
        this.accountByReceiverEmail = accountByReceiverEmail;
    }
}
