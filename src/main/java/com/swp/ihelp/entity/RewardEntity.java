package com.swp.ihelp.entity;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.event.EventEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reward", schema = "ihelp", catalog = "")
@Data
public class RewardEntity {

    @Id
    @Column(name = "id", nullable = false, length = 320)
    private String id;

    @Basic
    @Column(name = "title", nullable = true, length = 100)
    private String title;

    @Basic
    @Column(name = "description", nullable = true, length = 500)
    private String description;

    @Basic
    @Column(name = "point", nullable = true)
    private int point;

    @Basic
    @Column(name = "created_date", nullable = true)
    private long createdDate;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    private EventEntity eventByEventId;

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    private AccountEntity accountByAccountEmail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RewardEntity that = (RewardEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(point, that.point) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, point, createdDate);
    }

}
