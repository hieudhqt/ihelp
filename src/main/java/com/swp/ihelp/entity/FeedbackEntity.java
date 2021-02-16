package com.swp.ihelp.entity;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.event.EventEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "feedback", schema = "ihelp", catalog = "")
@Data
public class FeedbackEntity {

    @Id
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Basic
    @Column(name = "rating", nullable = true)
    private int rating;

    @Basic
    @Column(name = "comment", nullable = true, length = 200)
    private String comment;

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
        FeedbackEntity that = (FeedbackEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(rating, that.rating) &&
                Objects.equals(comment, that.comment) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rating, comment, createdDate);
    }

}
