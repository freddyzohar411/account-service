package com.avensys.rts.accountservice.payloadnewrequest;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: Koh He Xiang This class is used to store the form submission request
 * parameters for account service
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FormSubmissionsRequestDTO {
	private Integer formId;
	private Integer userId;
	private JsonNode submissionData;
	private Integer entityId;
	private String entityType;
}
