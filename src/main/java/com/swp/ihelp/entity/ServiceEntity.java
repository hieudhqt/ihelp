package com.swp.ihelp.entity;

import com.swp.ihelp.app.account.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "service", schema = "ihelp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceEntity {

    // ID format: SV_0000x
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "service_seq")
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

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    private AccountEntity accountByAccountEmail;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private StatusEntity statusByStatusId;

    @ManyToOne
    @JoinColumn(name = "service_type_id", referencedColumnName = "id", nullable = false)
    private ServiceTypeEntity serviceTypeByServiceTypeId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceEntity that = (ServiceEntity) o;
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
