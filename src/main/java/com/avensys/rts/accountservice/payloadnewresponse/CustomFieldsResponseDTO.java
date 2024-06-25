package com.avensys.rts.accountservice.payloadnewresponse;

import java.util.List;

import com.avensys.rts.accountservice.payloadnewrequest.FilterDTO;
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
	private String type;
	private List<String> columnName;
	private Integer createdBy;
	private Integer updatedBy;
	private List<FilterDTO> filters;
}
