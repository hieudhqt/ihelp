package com.swp.ihelp.app.feedback;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.event.EventEntity;
import com.swp.ihelp.app.feedbackcategory.FeedbackCategoryEntity;
import com.swp.ihelp.app.service.ServiceEntity;
import com.swp.ihelp.app.status.StatusEntity;
import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "feedback", schema = "ihelp")
@Data
@Accessors(chain = true)
public class FeedbackEntity {

    // ID format: FB_0000x
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feedback_seq")
    @GenericGenerator(
            name = "feedback_seq",
            strategy = "com.swp.ihelp.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "FB_"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
            }
    )
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Basic
    @Column(name = "rating", nullable = true)
    private Integer rating;

    @Basic
    @Column(name = "comment", nullable = true, length = 200)
    private String comment;

    @Basic
    @Column(name = "created_date", nullable = true)
    private Timestamp createdDate;

    @Basic
    @Column(name = "reason", nullable = true)
    private String reason;

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    private AccountEntity account;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id", nullable = true)
    private EventEntity event;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = true)
    private ServiceEntity service;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private StatusEntity status;

    @ManyToOne
    @JoinColumn(name = "manager_email", referencedColumnName = "email", nullable = true)
    private AccountEntity managerAccount;

    @ManyToOne
    @JoinColumn(name = "feedback_category_id", referencedColumnName = "id", nullable = true)
    private FeedbackCategoryEntity feedbackCategory;

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
