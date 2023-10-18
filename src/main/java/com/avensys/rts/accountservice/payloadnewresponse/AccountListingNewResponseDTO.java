package com.avensys.rts.accountservice.payloadnewresponse;

import com.avensys.rts.accountservice.entity.AccountNewEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountListingNewResponseDTO {
    private Integer totalPages;
    private Long totalElements;
    private Integer page;
    private Integer pageSize;

    private List<AccountNewListingDataDTO> accounts;
}
