package com.avensys.rts.accountservice.payloadnewresponse;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountListingDataDTO {

    private Integer id;
    private JsonNode accountSubmissionData;
    private JsonNode commercialSubmissionData;
    private String accountNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String accountCountry;

    public AccountListingDataDTO(AccountEntity accountEntity) {
        this.id = accountEntity.getId();
        this.accountSubmissionData = accountEntity.getAccountSubmissionData();
        this.commercialSubmissionData = accountEntity.getCommercialSubmissionData();
        this.accountNumber = accountEntity.getAccountNumber();
        this.createdAt = accountEntity.getCreatedAt();
        this.updatedAt = accountEntity.getUpdatedAt();
        this.accountCountry = accountEntity.getAccountCountry();
    }

    private String createdByName;
    private String updatedByName;

}
