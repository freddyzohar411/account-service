package com.avensys.rts.accountservice.payloadnewrequest;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: Koh He Xiang This class is used to store the request parameters for
 * the account listing api
 */
@Setter
@Getter
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
