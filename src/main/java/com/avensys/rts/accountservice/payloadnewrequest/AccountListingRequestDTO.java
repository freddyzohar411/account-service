package com.avensys.rts.accountservice.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Author: Koh He Xiang
 * This class is used to store the request parameters for the account listing api
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountListingRequestDTO {
    private Integer page = 0;
    private Integer pageSize = 5;
    private String sortBy;
    private String sortDirection;
    private String searchTerm;
    private List<String> searchFields;
}
