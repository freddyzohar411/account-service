package com.avensys.rts.accountservice.payloadrequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * author: Koh He Xiang
 * This is the DTO class for the address request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {

    @Length(max = 100)
    private String line1;

    @Length(max = 100)
    private String line2;

    @Length(max = 100)
    private String line3;

    @Length(max = 20)
    private String city;

    @Length(max = 20)
    private String country;

    @Length(max = 20)
    private String postalCode;

    @NotNull
    private Short type;

    @NotNull
    private int entityId;

    @NotEmpty
    @Length(max = 15)
    private String entityType;

}
