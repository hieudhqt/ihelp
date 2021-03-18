package com.swp.ihelp.app.account.request;

import com.swp.ihelp.app.account.AccountEntity;
import com.swp.ihelp.app.entity.AccountStatusEntity;
import com.swp.ihelp.app.entity.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SignUpRequest implements Serializable {

    private String email;

    private String password;

    private String fullname;

    private String phone;

    private Date dateOfBirth;

    private boolean gender;

    public static AccountEntity convertToEntity(SignUpRequest request) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        RoleEntity signUpRole = new RoleEntity("user", "User");
        AccountStatusEntity signUpStatus = new AccountStatusEntity("1", "valid");
        return new AccountEntity()
                .setEmail(request.getEmail())
                .setPassword(encoder.encode(request.getPassword()))
                .setFullName(request.getFullname())
                .setPhone(request.getPhone())
                .setDateOfBirth(request.getDateOfBirth())
                .setGender(request.isGender())
                .setBalancePoint(0)
                .setContributionPoint(0)
                .setCreatedDate(new Date(System.currentTimeMillis()))
                .setRole(signUpRole)
                .setStatus(signUpStatus);
    }
}
