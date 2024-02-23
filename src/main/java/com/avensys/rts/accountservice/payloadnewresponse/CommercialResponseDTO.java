package com.avensys.rts.accountservice.payloadnewresponse;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommercialResponseDTO {
	private Integer id;
	private String msp;
	private String markUp;
	private String submissionData;
	private Integer formId;
	private JsonNode commercialSubmissionData;
}
