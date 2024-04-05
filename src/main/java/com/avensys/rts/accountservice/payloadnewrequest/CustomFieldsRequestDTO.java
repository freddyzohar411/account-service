package com.avensys.rts.accountservice.payloadnewrequest;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldsRequestDTO {

	private String name;
	private String type;
	private List<String> columnName;
}
