package com.avensys.rts.accountservice.payloadnewrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Author: Koh He Xiang
 * This class is used to store the request parameters for the new account create api
 */
@Data
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
