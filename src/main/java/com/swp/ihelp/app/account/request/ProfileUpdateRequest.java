package com.swp.ihelp.app.account.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest implements Serializable {

    private String email;

    private String fullname;

    private String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Ho-Chi-Minh")
    private Date dateOfBirth;

    private Boolean gender;

}
