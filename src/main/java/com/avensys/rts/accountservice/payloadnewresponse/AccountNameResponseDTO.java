package com.avensys.rts.accountservice.payloadnewresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountNameResponseDTO {
	private Integer id;
	private String name;
}