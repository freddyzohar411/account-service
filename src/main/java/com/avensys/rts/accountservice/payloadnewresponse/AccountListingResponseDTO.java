package com.avensys.rts.accountservice.payloadnewresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountListingResponseDTO {
    private Integer totalPages;
    private Long totalElements;
    private Integer page;
    private Integer pageSize;

    private List<AccountListingDataDTO> accounts;
}
