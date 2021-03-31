package com.swp.ihelp.app.notification;

import com.swp.ihelp.app.account.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "notification", schema = "ihelp", catalog = "")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NotificationEntity {

    @Id
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "title")
    private String title;

    @Basic
    @Column(name = "message")
    private String message;

    @Basic
    @Column(name = "date")
    private Timestamp date;

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email")
    private AccountEntity accountEntity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationEntity that = (NotificationEntity) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(message, that.message) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, message, date);
    }
}
