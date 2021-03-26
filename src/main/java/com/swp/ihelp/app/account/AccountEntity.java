package com.swp.ihelp.app.account;

import com.swp.ihelp.app.entity.AccountStatusEntity;
import com.swp.ihelp.app.entity.RoleEntity;
import com.swp.ihelp.app.eventjointable.EventHasAccountEntity;
import com.swp.ihelp.app.servicejointable.ServiceHasAccountEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "account", schema = "ihelp")
@Data
@Accessors(chain = true)
public class AccountEntity {

    @Id
    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Basic
    @Column(name = "password", nullable = true, length = 60)
    private String password;

    @Basic
    @Column(name = "full_name", nullable = true, length = 255)
    private String fullName;

    @Basic
    @Column(name = "phone", nullable = true, length = 20)
    private String phone;

    @Basic
    @Column(name = "date_of_birth", nullable = true)
    private Date dateOfBirth;

    @Basic
    @Column(name = "gender", nullable = true)
    private Boolean gender;

    @Basic
    @Column(name = "balance_point", nullable = true)
    private Integer balancePoint;

    @Basic
    @Column(name = "contribution_point", nullable = true)
    private Integer contributionPoint;

    @Basic
    @Column(name = "created_date", nullable = true)
    private Date createdDate;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private RoleEntity role;

    @ManyToOne
    @JoinColumn(name = "account_status_id", referencedColumnName = "id", nullable = false)
    private AccountStatusEntity status;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "account",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH}
    )
    private Set<EventHasAccountEntity> EventAccount = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "account",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH}
    )
    private Set<ServiceHasAccountEntity> ServiceAccount = new HashSet<>();

    public void addBalancePoint(int point) {
        this.balancePoint += point;
    }

    public void decreaseBalancePoint(int point) {
        this.balancePoint -= point;
    }

    public void addContributionPoint(int point) {
        this.contributionPoint += point;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountEntity that = (AccountEntity) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(password, that.password) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(dateOfBirth, that.dateOfBirth) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(balancePoint, that.balancePoint) &&
                Objects.equals(contributionPoint, that.contributionPoint) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, fullName, phone, dateOfBirth, gender, balancePoint, contributionPoint, createdDate);
    }
}
