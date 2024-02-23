package com.avensys.rts.accountservice.payloadnewresponse;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountNewResponseDTO {
	private Integer id;
	private String name;
	private String accountCountry;
	private Integer formId;
	private String submissionData;
	private String createdBy;
	private String updatedBy;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private JsonNode accountSubmissionData;

}
