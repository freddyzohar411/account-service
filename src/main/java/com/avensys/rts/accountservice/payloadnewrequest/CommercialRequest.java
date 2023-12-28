package com.avensys.rts.accountservice.payloadnewrequest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Author: Koh He Xiang
 * This class is used to store the request parameters for the new account commercial create api
 */
@Data
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
	 * author: Koh He Xiang
	 * This is the DTO class for the document delete request
	 */
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DocumentDeleteRequestDTO {
		private String entityType;
		private Integer entityId;
	}

	/**
	 * author: Koh He Xiang
	 * This is the DTO class for the document request
	 */
	@Data
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
