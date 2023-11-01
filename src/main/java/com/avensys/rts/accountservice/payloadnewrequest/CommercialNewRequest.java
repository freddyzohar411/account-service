package com.avensys.rts.accountservice.payloadnewrequest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: Koh He Xiang
 * This class is used to store the request parameters for the new account commercial create api
 */
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
