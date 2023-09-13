package com.avensys.rts.accountservice.payloadresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountUserResponseDTO {
    private AccountResponseDTO account;
    private UserResponseDTO user;
}
