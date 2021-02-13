package com.swp.ihelp.app.event;

import com.swp.ihelp.app.entity.AccountEntity;
import com.swp.ihelp.app.eventjointable.EventHasAccountEntity;
import com.swp.ihelp.app.entity.StatusEntity;
import com.swp.ihelp.app.eventcategory.EventCategoryEntity;
import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.*;

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
    @GenericGenerator(
            name = "event_seq",
            strategy = "com.swp.ihelp.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "EV_"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
            }
    )
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

    //    @ManyToMany(fetch = FetchType.LAZY,
//            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
//                    CascadeType.DETACH, CascadeType.REFRESH})
//    @JoinTable(name = "event_has_account",
//    joinColumns = @JoinColumn(name = "event_id"),
//    inverseJoinColumns = @JoinColumn(name = "account_email"))
    @OneToMany(
            mappedBy = "event",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH}
    )
    private Set<EventHasAccountEntity> EventAccount = new HashSet<>();

//    public void addAccount(AccountEntity accountEntity) {
//        if (EventAccount == null) {
//            EventAccount = new HashSet<>();
//        }
//        EventAccount.add(accountEntity);
//    }

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

