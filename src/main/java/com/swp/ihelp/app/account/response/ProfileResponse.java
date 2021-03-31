package com.swp.ihelp.app.account.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.account.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ProfileResponse implements Serializable {

    private String email;

    private String fullname;

    private String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    private Date dateOfBirth;

    private String imageUrl;

    private Boolean gender;

    private Integer balancePoint;

    private Integer contributionPoint;

    private int totalJoinedEvents;

    private int totalHostEvents;

    private int totalUsedServices;

    private int totalHostServices;

    public ProfileResponse(AccountEntity accountEntity) {
        this.email = accountEntity.getEmail();
        this.fullname = accountEntity.getFullName();
        this.phone = accountEntity.getPhone();
        this.dateOfBirth = accountEntity.getDateOfBirth();
        this.gender = accountEntity.getGender();
        this.balancePoint = accountEntity.getBalancePoint();
        this.contributionPoint = accountEntity.getContributionPoint();
    }

}
