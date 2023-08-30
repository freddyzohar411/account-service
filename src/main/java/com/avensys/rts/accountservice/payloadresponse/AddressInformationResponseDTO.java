package com.avensys.rts.accountservice.payloadresponse;

import com.avensys.rts.accountservice.payloadrequest.AddressDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Koh He Xiang
 * This is the DTO class for the address information response
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressInformationResponseDTO {
    @Valid
    private AddressResponseDTO address;
    @Valid
    private AddressResponseDTO billingAddress;
}
