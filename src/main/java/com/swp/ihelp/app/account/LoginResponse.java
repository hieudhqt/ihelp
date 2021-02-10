package com.swp.ihelp.app.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class LoginResponse implements Serializable {

    private String email;

    private String token;

    private String role;

}
