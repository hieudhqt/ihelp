package com.swp.ihelp.app.account.response;

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

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    private String accessToken;

    private String email;

    private String imageUrl;

    private String role;

}
