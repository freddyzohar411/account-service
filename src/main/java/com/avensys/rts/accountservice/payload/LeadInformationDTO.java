package com.avensys.rts.accountservice.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * author: Koh He Xiang
 * This is the DTO class for the account information
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadInformationDTO {

    @NotEmpty(message = "Sales name cannot be empty")
    @Length(max = 50)
    private String salesName;

    @Length(max = 50)
    private String leadSource;

    @NotEmpty(message = "Account name cannot be empty")
    @Length(max = 50)
    private String accountName;

}
