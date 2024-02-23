package com.avensys.rts.accountservice.payloadnewrequest;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: Koh He Xiang This class is used to store the request parameters for
 * the new account commercial create api
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommercialRequest {
	private String msp;
	private String markUp;
	private String entityType;
	private Integer entityId;

	// Form Submission
	private String formData;
	private Integer formId;

	/**
	 * author: Koh He Xiang This is the DTO class for the document delete request
	 */
	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DocumentDeleteRequestDTO {
		private String entityType;
		private Integer entityId;
	}

	/**
	 * author: Koh He Xiang This is the DTO class for the document request
	 */
	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DocumentRequestDTO {
		private String type;
		private String title;
		private String description;
		private Integer entityId;
		private String entityType;
		MultipartFile file;
	}
}
