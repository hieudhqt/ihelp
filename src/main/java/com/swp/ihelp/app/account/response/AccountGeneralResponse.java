package com.swp.ihelp.app.account.response;

import com.swp.ihelp.app.account.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AccountGeneralResponse implements Serializable {

    private String email;

    private String fullname;

    private String phone;

    private long dateOfBirth;

    private boolean gender;

    private int balancePoint;

    private int cumulativePoint;

    private long createdDate;

    public AccountGeneralResponse(AccountEntity accountEntity) {
        this.email = accountEntity.getEmail();
        this.fullname = accountEntity.getFullName();
        this.phone = accountEntity.getPhone();
        this.dateOfBirth = accountEntity.getDateOfBirth();
        this.gender = accountEntity.getGender();
        this.balancePoint = accountEntity.getBalancePoint();
        this.cumulativePoint = accountEntity.getCumulativePoint();
        this.createdDate = accountEntity.getCreatedDate();
    }
}
