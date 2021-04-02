package com.swp.ihelp.app.account.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class LoginResponse implements Serializable {

    private String accessToken;

    private String email;

    private String fullName;

    private String imageUrl;

    private String role;

    private List<String> evaluateRequiredEvents;

}
