package com.swp.ihelp.app.point;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "point", schema = "ihelp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "point_seq")
    @GenericGenerator(
            name = "point_seq",
            strategy = "com.swp.ihelp.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "P_"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d")
            }
    )
    @Column(name = "id", nullable = false, length = 20)
    private String id;

    @Basic
    @Column(name = "amount", nullable = true)
    private Integer amount;

    @Basic
    @Column(name = "description", nullable = true, length = 1000)
    private String description;

    @Basic
    @Column(name = "is_received", nullable = true)
    private Boolean isReceived;

    @Basic
    @Column(name = "created_date", nullable = true)
    private Timestamp createdDate;

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    private AccountEntity account;

}
