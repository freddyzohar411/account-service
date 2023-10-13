package com.avensys.rts.accountservice.payloadnewresponse;

import com.avensys.rts.accountservice.payloadresponse.UserResponseDTO;
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
    private Integer formId;
    private String submissionData;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
