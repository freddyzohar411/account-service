package com.avensys.rts.accountservice.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountListingDeleteRequestDTO {
	private List<Integer> accountIds;
}
