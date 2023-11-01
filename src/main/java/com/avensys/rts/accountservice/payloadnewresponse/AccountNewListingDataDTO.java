package com.avensys.rts.accountservice.payloadnewresponse;

import com.avensys.rts.accountservice.entity.AccountNewEntity;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountNewListingDataDTO {

    private Integer id;
    private JsonNode accountSubmissionData;
    private JsonNode commercialSubmissionData;
    private String accountNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String accountCountry;

    public AccountNewListingDataDTO(AccountNewEntity accountNewEntity) {
        this.id = accountNewEntity.getId();
        this.accountSubmissionData = accountNewEntity.getAccountSubmissionData();
        this.commercialSubmissionData = accountNewEntity.getCommercialSubmissionData();
        this.accountNumber = accountNewEntity.getAccountNumber();
        this.createdAt = accountNewEntity.getCreatedAt();
        this.updatedAt = accountNewEntity.getUpdatedAt();
        this.accountCountry = accountNewEntity.getAccountCountry();
    }

    private String createdByName;
    private String updatedByName;

}
