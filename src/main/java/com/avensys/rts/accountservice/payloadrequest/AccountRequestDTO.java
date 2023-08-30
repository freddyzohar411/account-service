package com.avensys.rts.accountservice.payloadrequest;

import jakarta.validation.Valid;
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
    private int id;
    @Valid
    private AccountInformationDTO accountInformation;
    @Valid
    private LeadInformationDTO leadInformation;
    @Valid
    private AddressInformationDTO addressInformation;
    private String accountRemarks;
}
