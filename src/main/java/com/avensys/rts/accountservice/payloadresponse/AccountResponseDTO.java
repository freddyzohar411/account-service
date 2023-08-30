package com.avensys.rts.accountservice.payloadresponse;

import com.avensys.rts.accountservice.payloadrequest.AccountInformationDTO;
import com.avensys.rts.accountservice.payloadrequest.AddressInformationDTO;
import com.avensys.rts.accountservice.payloadrequest.LeadInformationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private int id;
    AccountInformationResponseDTO accountInformation;
    LeadInformationResponseDTO leadInformation;
    AddressInformationResponseDTO addressInformation;
    private String accountRemarks;
}
