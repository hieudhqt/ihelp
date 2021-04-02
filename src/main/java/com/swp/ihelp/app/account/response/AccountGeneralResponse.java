package com.swp.ihelp.app.account.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AccountGeneralResponse implements Serializable {

    private String email;

    private String fullname;

    private String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    private Date dateOfBirth;

    private String imageUrl;

    private Boolean gender;

    private RoleEntity role;

    private Integer balancePoint;

    private Integer contributionPoint;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_ Minh")
    private Date createdDate;

    public AccountGeneralResponse(AccountEntity accountEntity) {
        this.email = accountEntity.getEmail();
        this.fullname = accountEntity.getFullName();
        this.phone = accountEntity.getPhone();
        this.dateOfBirth = accountEntity.getDateOfBirth();
        this.gender = accountEntity.getGender();
        this.role = accountEntity.getRole();
        this.balancePoint = accountEntity.getBalancePoint();
        this.contributionPoint = accountEntity.getContributionPoint();
        this.createdDate = accountEntity.getCreatedDate();
    }

    public static List<AccountGeneralResponse> convertToListResponse(List<AccountEntity> entityList) {
        List<AccountGeneralResponse> result = new ArrayList<>();
        for (AccountEntity accountEntity : entityList) {
            result.add(new AccountGeneralResponse(accountEntity));
        }
        return result;
    }
}
