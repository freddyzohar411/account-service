package com.avensys.rts.accountservice.payloadrequest;

import com.avensys.rts.accountservice.annotation.FileSize;
import com.avensys.rts.accountservice.annotation.URLOrNull;
import com.avensys.rts.accountservice.annotation.ValidPdfFile;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

/**
 * author: Koh He Xiang
 * This is the DTO class for the account information request
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
    @Length(max = 10)
    private String accountRating;

    private Integer accountIndustry;

    private Integer subIndustry;

    @PositiveOrZero(message = "Number of employees cannot be negative")
    private Integer noOfEmployees;

    @PositiveOrZero(message = "Revenue amount cannot be negative")
    private double revenueAmt;

    private Integer revenueCur;

    private Integer parentCompany;

    @Length(max = 250)
    @URLOrNull(message = "Website must be a valid URL")
    private String website;

    @NotEmpty(message = "Account source cannot be empty")
    @Length(max = 50)
    private String accountSource;

    @PositiveOrZero(message = "Landline country cannot be negative")
    private Integer landlineCountry;

    @PositiveOrZero(message = "Landline country cannot be negative")
    private Integer landlineNumber;

    @Length(max = 50)
    private String secondaryOwner;

    @NotNull(message = "MSA cannot be empty")
    private Integer msa;

//    @NotNull(message = "File cannot be null")
    @ValidPdfFile(message = "File must be a PDF file")
    @FileSize(maxSize = 1, message = "File size must be less than 1MB")
    private MultipartFile uploadAgreement;
}
