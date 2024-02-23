package com.avensys.rts.accountservice.payloadnewresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountNameResponseDTO {
	private Integer id;
	private String name;
}