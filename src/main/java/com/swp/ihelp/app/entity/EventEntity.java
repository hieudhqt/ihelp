package com.swp.ihelp.app.entity;

import com.swp.ihelp.app.account.AccountEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "event", schema = "ihelp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventEntity {
    // ID format: EV_0000x
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_seq")
    private String id;

    @Basic
    @Column(name = "title", nullable = true, length = 100)
    private String title;

    @Basic
    @Column(name = "description", nullable = true, length = 1000)
    private String description;

    @Basic
    @Column(name = "location", nullable = true, length = 300)
    private String location;

    @Basic
    @Column(name = "quota", nullable = true)
    private int quota;

    @Basic
    @Column(name = "point", nullable = true)
    private int point;
    @Basic
    @Column(name = "created_date", nullable = true)
    private long createdDate;
    @Basic
    @Column(name = "start_date", nullable = true)
    private long startDate;
    @Basic
    @Column(name = "end_date", nullable = true)
    private long endDate;

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    private AccountEntity accountByAccountEmail;

    @ManyToOne
    @JoinColumn(name = "event_category_id", referencedColumnName = "id", nullable = false)
    private EventCategoryEntity eventCategoryByEventCategoryId;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private StatusEntity statusByStatusId;

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
}
