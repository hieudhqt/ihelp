package com.swp.ihelp.app.feedback.request;

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
public class RejectFeedbackRequest implements Serializable {

    @NotBlank
    private String feedbackId;

    @NotBlank
    private String managerEmail;

    @NotBlank
    private String reason;

}
