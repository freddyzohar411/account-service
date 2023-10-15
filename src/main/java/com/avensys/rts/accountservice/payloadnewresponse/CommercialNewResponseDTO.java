package com.avensys.rts.accountservice.payloadnewresponse;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommercialNewResponseDTO {
    private Integer id;
    private String msp;
    private String markUp;
    private String submissionData;
    private Integer formId;
}
