package com.swp.ihelp.app.service;

import com.swp.ihelp.app.entity.AccountEntity;
import com.swp.ihelp.app.entity.StatusEntity;
import com.swp.ihelp.app.servicejointable.ServiceHasAccountEntity;
import com.swp.ihelp.app.servicetype.ServiceTypeEntity;
import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service", schema = "ihelp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ServiceEntity {

    // ID format: SV_0000x
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "service_seq")
    @GenericGenerator(
            name = "service_seq",
            strategy = "com.swp.ihelp.config.StringPrefixedSequenceIdGenerator",
            parameters = {
                    @Parameter(name = StringPrefixedSequenceIdGenerator.INCREMENT_PARAM, value = "50"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.VALUE_PREFIX_PARAMETER, value = "SV_"),
                    @Parameter(name = StringPrefixedSequenceIdGenerator.NUMBER_FORMAT_PARAMETER, value = "%05d"),
            }
    )
    @Column(name = "id", nullable = false, length = 20)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    private AccountEntity authorAccount;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private StatusEntity status;

    @ManyToOne
    @JoinColumn(name = "service_type_id", referencedColumnName = "id", nullable = false)
    private ServiceTypeEntity serviceType;

    @OneToMany(
            mappedBy = "service",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH}
    )
    private Set<ServiceHasAccountEntity> ServiceAccount = new HashSet<>();

}
