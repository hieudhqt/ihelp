package com.swp.ihelp.app.entity;

import com.swp.ihelp.app.account.AccountEntity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "account_image", schema = "ihelp", catalog = "")
public class AccountImageEntity {
    private String id;
    private String imageUrl;
    private String type;
    private AccountEntity accountByAccountEmail;

    @Id
    @Column(name = "id", nullable = false, length = 45)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "image_url", nullable = false, length = 2083)
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Basic
    @Column(name = "type", nullable = false, length = 10)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountImageEntity that = (AccountImageEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageUrl, type);
    }

    @ManyToOne
    @JoinColumn(name = "account_email", referencedColumnName = "email", nullable = false)
    public AccountEntity getAccountByAccountEmail() {
        return accountByAccountEmail;
    }

    public void setAccountByAccountEmail(AccountEntity accountByAccountEmail) {
        this.accountByAccountEmail = accountByAccountEmail;
    }
}
