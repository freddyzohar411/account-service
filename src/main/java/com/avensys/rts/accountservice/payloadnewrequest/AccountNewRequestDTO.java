package com.avensys.rts.accountservice.payloadnewrequest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountNewRequestDTO {
    private String accountName;
    private String accountRating;
    private String accountRemarks;
    private String accountSource;
    private String accountStatus;
    private String addressCity;
    private String addressCountry;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressPostalCode;
    private String billingAddress;
    private String billingAddressCountry;
    private String billingAddressLine1;
    private String billingAddressLine2;
    private String billingAddressLine3;
    private String billingAddressPostalCode;
    private String billingCity;
    private String industry;
    private Integer landlineCountry;
    private Integer landlineNumber;
    private String leadAccountName;
    private String leadSalesName;
    private String leadSource;
    private String msa;
    private Integer noOfEmployees;
    private String parentCompany;
    private Integer revenue;
    private Integer revenueCurrency;
    private String salesName;
    private String secondaryOwner;
    private String subIndustry;
    private String website;
    private MultipartFile uploadAgreement;

    // Form Submission
    private String formData;
    private Integer formId;

    // Added -27102023
    private String accountCountry;
}
