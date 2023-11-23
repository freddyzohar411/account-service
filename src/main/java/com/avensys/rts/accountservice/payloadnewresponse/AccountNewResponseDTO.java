package com.avensys.rts.accountservice.payloadnewresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountNewResponseDTO {
    private Integer id;
    private String name;
    private String accountCountry;
    private Integer formId;
    private String submissionData;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private JsonNode accountSubmissionData;

}
