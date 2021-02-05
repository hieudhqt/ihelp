package com.swp.ihelp.app.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "reward", schema = "ihelp", catalog = "")
public class RewardEntity {
    private String id;
    private String title;
    private String description;
    private Object point;
    private Object createdDate;
    private EventEntity eventByEventId;
    private AccountEntity accountByAccountEmail;

    @Id
    @Column(name = "id", nullable = false, length = 320)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "title", nullable = true, length = 100)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "description", nullable = true, length = 500)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "point", nullable = true)
    public Object getPoint() {
        return point;
    }

    public void setPoint(Object point) {
        this.point = point;
    }

    @Basic
    @Column(name = "created_date", nullable = true)
    public Object getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Object createdDate) {
        this.createdDate = createdDate;
    }

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

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = false)
    public EventEntity getEventByEventId() {
        return eventByEventId;
    }

    public void setEventByEventId(EventEntity eventByEventId) {
        this.eventByEventId = eventByEventId;
    }

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    public AccountEntity getAccountByAccountEmail() {
        return accountByAccountEmail;
    }

    public void setAccountByAccountEmail(AccountEntity accountByAccountEmail) {
        this.accountByAccountEmail = accountByAccountEmail;
    }
}
