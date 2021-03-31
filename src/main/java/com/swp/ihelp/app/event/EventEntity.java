package com.swp.ihelp.app.event;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.eventcategory.EventCategoryEntity;
import com.swp.ihelp.app.eventjointable.EventHasAccountEntity;
import com.swp.ihelp.app.image.ImageEntity;
import com.swp.ihelp.app.status.StatusEntity;
import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "event", schema = "ihelp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)
@DynamicUpdate
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
    @Column(name = "lng", nullable = true, length = 30)
    private String longitude;

    @Basic
    @Column(name = "lat", nullable = true, length = 30)
    private String latitude;

    @Basic
    @Column(name = "quota", nullable = true)
    private Integer quota;

    @Basic
    @Column(name = "point", nullable = true)
    private Integer point;

    @Basic
    @Column(name = "created_date", nullable = true)
    private Timestamp createdDate;

    @Basic
    @Column(name = "start_date", nullable = true)
    private Timestamp startDate;

    @Basic
    @Column(name = "end_date", nullable = true)
    private Timestamp endDate;

    @Basic
    @Column(name = "is_onsite", nullable = false)
    private Boolean isOnsite;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    private AccountEntity authorAccount;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "event_category_has_event",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "event_category_id"))
    private Set<EventCategoryEntity> eventCategories = new HashSet<>();

    public void addCategory(EventCategoryEntity eventCategoryEntity) {
        if (eventCategories.contains(eventCategoryEntity)) {
            return;
        }
        eventCategories.add(eventCategoryEntity);
    }

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private StatusEntity status;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "event",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH}
    )
    @Getter(value = AccessLevel.NONE)
    private Set<EventHasAccountEntity> eventAccount = new HashSet<>();

    //Methods to add and remove EventHasAccount to avoid infinite loop.
    public Set<EventHasAccountEntity> getEventAccount() {
        return Collections.unmodifiableSet(eventAccount);
    }

    public void addEventAccount(EventHasAccountEntity eventHasAccount) {
        eventHasAccount.setEvent(this);
    }

    public void removeEventAccount(EventHasAccountEntity eventHasAccount) {
        eventHasAccount.setEvent(null);
    }

    public void internalAddEventAccount(EventHasAccountEntity eventHasAccount) {
        eventAccount.add(eventHasAccount);
    }

    public void internalRemoveEventAccount(EventHasAccountEntity eventHasAccount) {
        eventAccount.remove(eventHasAccount);
    }

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "event_has_image",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id"))
    private Set<ImageEntity> images = new HashSet<>();

    public void addImage(ImageEntity imageEntity) {
        if (images.contains(imageEntity)) {
            return;
        }
        images.add(imageEntity);
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_email", referencedColumnName = "email", nullable = true)
    private AccountEntity managerAccount;

    @Basic
    @Column(name = "reason", nullable = true)
    private String reason;

    public EventEntity(String id, Date startDate, Date endDate) {
        this.id = id;
        this.startDate = new Timestamp(startDate.getTime());
        this.endDate = new Timestamp(endDate.getTime());
    }
}

