package com.avensys.rts.accountservice.payloadnewresponse;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountListingResponseDTO {
	private Integer totalPages;
	private Long totalElements;
	private Integer page;
	private Integer pageSize;

	private List<AccountListingDataDTO> accounts;
}
