package com.swp.ihelp.app.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "account", schema = "ihelp")
public class AccountEntity {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private long dateOfBirth;
    private Boolean gender;
    private int balancePoint;
    private int cumulativePoint;
    private long createdDate;

    @Id
    @Column(name = "email", nullable = false, length = 320)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "password", nullable = true, length = 60)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "full_name", nullable = true, length = 255)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Basic
    @Column(name = "phone", nullable = true, length = 20)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "date_of_birth", nullable = true)
    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Basic
    @Column(name = "gender", nullable = true)
    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    @Basic
    @Column(name = "balance_point", nullable = true)
    public int getBalancePoint() {
        return balancePoint;
    }

    public void setBalancePoint(int balancePoint) {
        this.balancePoint = balancePoint;
    }

    @Basic
    @Column(name = "cumulative_point", nullable = true)
    public int getCumulativePoint() {
        return cumulativePoint;
    }

    public void setCumulativePoint(int cumulativePoint) {
        this.cumulativePoint = cumulativePoint;
    }

    @Basic
    @Column(name = "created_date", nullable = true)
    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
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
                Objects.equals(cumulativePoint, that.cumulativePoint) &&
                Objects.equals(createdDate, that.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, fullName, phone, dateOfBirth, gender, balancePoint, cumulativePoint, createdDate);
    }
}
