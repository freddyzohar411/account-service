package com.avensys.rts.accountservice.payload;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Koh He Xiang
 * This is the DTO class for the address information
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressInformationDTO {
    @Valid
    private AddressDTO address;
    private Boolean isSameAsBillingAddress = false;
    @Valid
    private AddressDTO billingAddress;
}
