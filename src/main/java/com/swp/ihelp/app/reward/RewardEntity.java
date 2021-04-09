package com.swp.ihelp.app.reward;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reward", schema = "ihelp")
@Data
public class RewardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reward_seq")
    @GenericGenerator(
            name = "reward_seq",
            strategy = "com.swp.ihelp.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "RW_"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
            }
    )
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
    private Timestamp createdDate;

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
