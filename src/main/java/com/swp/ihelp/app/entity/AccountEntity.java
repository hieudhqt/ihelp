package com.swp.ihelp.app.entity;

import com.swp.ihelp.app.eventjointable.EventHasAccountEntity;
import com.swp.ihelp.app.servicejointable.ServiceHasAccountEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "account", schema = "ihelp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
    private long dateOfBirth;

    @Basic
    @Column(name = "gender", nullable = true)
    private Boolean gender;

    @Basic
    @Column(name = "balance_point", nullable = true)
    private int balancePoint;

    @Basic
    @Column(name = "cumulative_point", nullable = true)
    private int cumulativePoint;

    @Basic
    @Column(name = "created_date", nullable = true)
    private long createdDate;

    //    @ManyToMany(fetch = FetchType.LAZY,
//            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
//                    CascadeType.DETACH, CascadeType.REFRESH})
//    @JoinTable(name = "event_has_account",
//            joinColumns = @JoinColumn(name = "account_email"),
//            inverseJoinColumns = @JoinColumn(name = "event_id"))
    @OneToMany(
            mappedBy = "account",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH}
    )
    private Set<EventHasAccountEntity> EventAccount = new HashSet<>();

    @OneToMany(
            mappedBy = "account",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                    CascadeType.DETACH, CascadeType.REFRESH}
    )
    private Set<ServiceHasAccountEntity> ServiceAccount = new HashSet<>();

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
                Objects.equals(cumulativePoint, that.cumulativePoint) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, fullName, phone, dateOfBirth, gender, balancePoint, cumulativePoint, createdDate);
    }
}
