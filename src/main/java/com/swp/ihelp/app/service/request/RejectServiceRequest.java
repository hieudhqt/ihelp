package com.swp.ihelp.app.service.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class RejectServiceRequest implements Serializable {
    @NotBlank
    private String serviceId;
    @NotBlank
    private String managerEmail;
    @NotBlank
    private String reason;
}
