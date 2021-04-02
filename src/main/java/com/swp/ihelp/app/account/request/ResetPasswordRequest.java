package com.swp.ihelp.app.account.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ResetPasswordRequest implements Serializable {

    private String email;

    private String oldPassword;

    private String newPassword;

}
