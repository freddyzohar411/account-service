package com.avensys.rts.accountservice.payload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Koh He Xiang
 * This class is used to store the data of the incoming Account request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDTO {
    @Valid
    private AccountInformationDTO accountInformation;
    @Valid
    private LeadInformationDTO leadInformation;
    @Valid
    private AddressInformationDTO addressInformation;
    private String accountRemarks;
}
