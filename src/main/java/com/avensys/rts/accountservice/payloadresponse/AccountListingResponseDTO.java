package com.avensys.rts.accountservice.payloadresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountListingResponseDTO {
    private Integer totalPages;
    private Long totalElements;
    private Integer page;
    private Integer pageSize;
    List<AccountUserResponseDTO> accounts;
}
