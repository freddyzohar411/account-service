package com.avensys.rts.accountservice.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommercialNewRequest {
    private String msp;
    private String markUp;
    private String entityType;
    private Integer entityId;

    // Form Submission
    private String formData;
    private Integer formId;
}
