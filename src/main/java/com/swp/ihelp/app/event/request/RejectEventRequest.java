package com.swp.ihelp.app.event.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class RejectEventRequest implements Serializable {
    @NotBlank
    private String eventId;
    @NotBlank
    private String managerEmail;
    @NotBlank
    private String reason;
}
