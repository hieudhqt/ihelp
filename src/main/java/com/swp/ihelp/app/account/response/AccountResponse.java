package com.swp.ihelp.app.account.response;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.entity.AccountStatusEntity;
import com.swp.ihelp.entity.RoleEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AccountResponse implements Serializable {
    private String email;
    private String fullName;
    private String phone;
    private long dateOfBirth;
    private boolean gender;
    private int balancePoint;
    private int cumulativePoint;
    private long createDate;
    private AccountStatusEntity status;
    private RoleEntity role;

    public AccountResponse(AccountEntity accountEntity) {
        this.email = accountEntity.getEmail();
        this.fullName = accountEntity.getFullName();
        this.phone = accountEntity.getPhone();
        this.dateOfBirth = accountEntity.getDateOfBirth();
        this.gender = accountEntity.getGender();
        this.balancePoint = accountEntity.getBalancePoint();
        this.cumulativePoint = accountEntity.getCumulativePoint();
        this.createDate = accountEntity.getCreatedDate();
        this.status = accountEntity.getStatus();
        this.role = accountEntity.getRole();
    }

    public AccountEntity convertToEntity() {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setEmail(this.email);
        accountEntity.setFullName(this.fullName);
        accountEntity.setPhone(this.phone);
        accountEntity.setDateOfBirth(this.dateOfBirth);
        accountEntity.setGender(this.gender);
        accountEntity.setBalancePoint(this.balancePoint);
        accountEntity.setCumulativePoint(this.cumulativePoint);
        accountEntity.setCreatedDate(this.createDate);
        accountEntity.setStatus(this.status);
        accountEntity.setRole(this.role);
        return accountEntity;
    }
}
