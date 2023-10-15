package com.avensys.rts.accountservice.payloadnewresponse;

import com.avensys.rts.accountservice.entity.AccountNewEntity;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormSubmissionsResponseDTO {
    private Integer id;
    private String formId;
    private Integer userId;
    private JsonNode submissionData;
    private Integer entityId;
    private String entityType;
}
