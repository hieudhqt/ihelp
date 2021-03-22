package com.swp.ihelp.app.service;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.image.ImageEntity;
import com.swp.ihelp.app.servicecategory.ServiceCategoryEntity;
import com.swp.ihelp.app.servicejointable.ServiceHasAccountEntity;
import com.swp.ihelp.app.status.StatusEntity;
import com.swp.ihelp.config.StringPrefixedSequenceIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "service", schema = "ihelp")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@DynamicUpdate
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
    @Column(name = "title", nullable = false, length = 100)
    @NotNull
    private String title;

    @Basic
    @Column(name = "description", nullable = true, length = 1000)
    private String description;

    @Basic
    @Column(name = "location", nullable = false, length = 300)
    private String location;

    @Basic
    @Column(name = "quota", nullable = false)
    private Integer quota;

    @Basic
    @Column(name = "point", nullable = false)
    private Integer point;

    @Basic
    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @Basic
    @Column(name = "start_date", nullable = false)
    private Timestamp startDate;

    @Basic
    @Column(name = "end_date", nullable = false)
    private Timestamp endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    private AccountEntity authorAccount;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private StatusEntity status;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "service_category_has_service",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "service_category_id"))
    private List<ServiceCategoryEntity> categories = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH})
    @JoinTable(name = "service_has_image",
            joinColumns = @JoinColumn(name = "service_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id"))
    private List<ImageEntity> images = new ArrayList<>();

    public void addCategory(ServiceCategoryEntity category) {
        if (categories.contains(category)) {
            return;
        }
        categories.add(category);
    }

    public void addImage(ImageEntity imageEntity) {
        if (images.contains(imageEntity)) {
            return;
        }
        images.add(imageEntity);
    }

    @OneToMany(
            mappedBy = "service",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH}
    )
    private Set<ServiceHasAccountEntity> serviceAccount = new HashSet<>();

    public Set<ServiceHasAccountEntity> getServiceAccount() {
        return Collections.unmodifiableSet(serviceAccount);
    }

    public void addServiceAccount(ServiceHasAccountEntity serviceHasAccount) {
        serviceHasAccount.setService(this);
    }

    public void removeServiceAccount(ServiceHasAccountEntity serviceHasAccount) {
        serviceHasAccount.setService(null);
    }

    public void internalAddServiceAccount(ServiceHasAccountEntity serviceHasAccount) {
        serviceAccount.add(serviceHasAccount);
    }

    public void internalRemoveServiceAccount(ServiceHasAccountEntity serviceHasAccount) {
        serviceAccount.remove(serviceHasAccount);
    }

}
