package com.avensys.rts.accountservice.payloadresponse;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressResponseDTO {

    private int id;

    private String line1;

    private String line2;

    private String line3;

    private String city;

    private String country;

    private String postalCode;

//    private Short type;
//
//    private int entityId;
//
//    private String entityType;
}
