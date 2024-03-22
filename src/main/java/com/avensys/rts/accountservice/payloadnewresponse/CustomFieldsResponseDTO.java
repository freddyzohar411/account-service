package com.avensys.rts.accountservice.payloadnewresponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldsResponseDTO {

	private Long id;
	private String name;
	private String columnName;
	private Integer createdBy;
	private Integer updatedBy;
}
