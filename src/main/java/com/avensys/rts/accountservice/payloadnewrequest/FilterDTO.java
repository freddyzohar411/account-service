package com.avensys.rts.accountservice.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterDTO {
	private String condition;
	private String field;
	private String label;
	private String value;
}
