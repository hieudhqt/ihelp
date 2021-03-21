package com.swp.ihelp.app.account.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest implements Serializable {

    private String fullname;

    private String phone;

    private Date dateOfBirth;

    private boolean gender;

}
