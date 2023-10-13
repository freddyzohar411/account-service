package com.avensys.rts.accountservice.payloadnewresponse;

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
