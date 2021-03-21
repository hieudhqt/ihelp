package com.swp.ihelp.app.event.request;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class EvaluationRequest implements Serializable {
    @NotBlank
    private String eventId;
    @NotBlank
    private String memberEmail;
    @Min(1)
    private int rating;
    private String comment;

}
