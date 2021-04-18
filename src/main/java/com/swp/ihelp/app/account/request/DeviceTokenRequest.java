package com.swp.ihelp.app.account.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DeviceTokenRequest implements Serializable {

    @NotBlank
    private String email;

    @NotBlank
    private String deviceToken;

}
