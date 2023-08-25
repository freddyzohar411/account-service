package com.avensys.rts.accountservice.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

/**
 * author: Koh He Xiang
 * This is the DTO class for the account information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInformationDTO {
    
    @NotEmpty(message = "Account name cannot be empty")
    @Length(max = 50)
    private String accountName;

    @Length(max = 50)
    private String salesName;

    @NotEmpty(message = "Account status cannot be empty")
    @Length(max = 10)
    private String accountStatus;

    @NotEmpty(message = "Account rating cannot be empty")
    @Length(max = 5)
    private String accountRating;

    @Length(max = 20)
    private String accountIndustry;

    @Length(max = 20)
    private String subIndustry;

    @PositiveOrZero(message = "Number of employees cannot be negative")
    private int noOfEmployees;

    @PositiveOrZero(message = "Revenue amount cannot be negative")
    private double revenueAmt;

    @Length(max = 10)
    private String revenueCur;

    @Length(max = 100)
    private String parentCompany;

    @URL
    @Length(max = 250)
    private String website;

    @NotEmpty(message = "Account source cannot be empty")
    @Length(max = 50)
    private String accountSource;

    @PositiveOrZero(message = "Landline country cannot be negative")
    private int landlineCountry;

    @PositiveOrZero(message = "Landline country cannot be negative")
    private int landlineNumber;

    @NotEmpty(message = "Secondary owner cannot be empty")
    @Length(max = 50)
    private String secondaryOwner;

    @NotNull(message = "MSA cannot be empty")
    private int msa;

    // Note: This cannot be empty change later
    private MultipartFile uploadAgreement;
}
