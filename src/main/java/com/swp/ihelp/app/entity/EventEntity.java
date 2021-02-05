package com.swp.ihelp.app.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "event", schema = "ihelp", catalog = "")
public class EventEntity {
    private String id;
    private String title;
    private String description;
    private String location;
    private Object quota;
    private Object point;
    private Object createdDate;
    private Object startDate;
    private Object endDate;
    private AccountEntity accountByAccountEmail;
    private EventCategoryEntity eventCategoryByEventCategoryId;

    @Id
    @Column(name = "id", nullable = false, length = 20)
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
    @Column(name = "description", nullable = true, length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "location", nullable = true, length = 300)
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Basic
    @Column(name = "quota", nullable = true)
    public Object getQuota() {
        return quota;
    }

    public void setQuota(Object quota) {
        this.quota = quota;
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

    @Basic
    @Column(name = "start_date", nullable = true)
    public Object getStartDate() {
        return startDate;
    }

    public void setStartDate(Object startDate) {
        this.startDate = startDate;
    }

    @Basic
    @Column(name = "end_date", nullable = true)
    public Object getEndDate() {
        return endDate;
    }

    public void setEndDate(Object endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventEntity that = (EventEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(location, that.location) &&
                Objects.equals(quota, that.quota) &&
                Objects.equals(point, that.point) &&
                Objects.equals(createdDate, that.createdDate) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, location, quota, point, createdDate, startDate, endDate);
    }

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    public AccountEntity getAccountByAccountEmail() {
        return accountByAccountEmail;
    }

    public void setAccountByAccountEmail(AccountEntity accountByAccountEmail) {
        this.accountByAccountEmail = accountByAccountEmail;
    }

    @ManyToOne
    @JoinColumn(name = "event_category_id", referencedColumnName = "id", nullable = false)
    public EventCategoryEntity getEventCategoryByEventCategoryId() {
        return eventCategoryByEventCategoryId;
    }

    public void setEventCategoryByEventCategoryId(EventCategoryEntity eventCategoryByEventCategoryId) {
        this.eventCategoryByEventCategoryId = eventCategoryByEventCategoryId;
    }
}
