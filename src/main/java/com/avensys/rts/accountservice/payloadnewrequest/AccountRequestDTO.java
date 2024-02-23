package com.avensys.rts.accountservice.payloadnewrequest;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: Koh He Xiang This class is used to store the request parameters for
 * the new account create api
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDTO {
	private String accountName;
	private MultipartFile uploadAgreement;
	private Boolean isDeleteFile = false;

	// Form Submission
	private String formData;
	private Integer formId;

	// Added -27102023
	private String accountCountry;
}
