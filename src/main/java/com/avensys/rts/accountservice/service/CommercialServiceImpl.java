package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.APIClient.FormSubmissionAPIClient;
import com.avensys.rts.accountservice.APIClient.UserAPIClient;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.entity.AccountNewEntity;
import com.avensys.rts.accountservice.payloadnewrequest.CommercialNewRequest;
import com.avensys.rts.accountservice.payloadnewrequest.FormSubmissionsRequestDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNewResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.CommercialNewResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.FormSubmissionsResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.UserResponseDTO;
import com.avensys.rts.accountservice.repository.AccountNewRepository;
import com.avensys.rts.accountservice.util.JwtUtil;
import com.avensys.rts.accountservice.util.MappingUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommercialServiceImpl implements CommercialService{

    private final AccountNewRepository accountRepository;

    @Autowired
    private FormSubmissionAPIClient formSubmissionAPIClient;

    @Autowired
    private UserAPIClient userAPIClient;

    public CommercialServiceImpl(AccountNewRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public CommercialNewResponseDTO createCommercial(Integer id, CommercialNewRequest commercialNewRequest) {
        AccountNewEntity accountEntityFound = accountRepository.findByIdAndDeleted(id, false, true)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountEntityFound.setMarkUp(commercialNewRequest.getMarkUp());
        accountEntityFound.setMsp(commercialNewRequest.getMsp());
        accountEntityFound.setCommercialFormId(commercialNewRequest.getFormId());

        // Save form data to form submission microservice
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = commercialRequestToFormSubmissionRequestDTO(commercialNewRequest);
        HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
        FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

        // Added - create commercial form Data
        accountEntityFound.setCommercialSubmissionData(formSubmissionData.getSubmissionData());

        accountEntityFound.setCommercialFormSubmissionId(formSubmissionData.getId());
        accountEntityFound.setIsDraft(false);
        return commercialEntityToCommercialNewResponseDTO(accountRepository.save(accountEntityFound));
    }

    @Override
    public CommercialNewResponseDTO getCommercial(Integer id) {
        Optional<AccountNewEntity> accountEntity = accountRepository.findByIdAndDeleted(id, false, true);
        if (accountEntity.isPresent()) {
            return commercialEntityToCommercialNewResponseDTO(accountEntity.get());
        }
        return null;
    }

    @Override
    public CommercialNewResponseDTO updateCommercial(Integer id, CommercialNewRequest commercialNewRequest) {
        AccountNewEntity accountEntityFound = accountRepository.findByIdAndDeleted(id, false, true)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountEntityFound.setMarkUp(commercialNewRequest.getMarkUp());
        accountEntityFound.setMsp(commercialNewRequest.getMsp());
        accountEntityFound.setCommercialFormId(commercialNewRequest.getFormId());

        // Update form data to form submission microservice
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = commercialRequestToFormSubmissionRequestDTO(commercialNewRequest);
        HttpResponse formSubmissionResponse = formSubmissionAPIClient.updateFormSubmission(accountEntityFound.getCommercialFormSubmissionId(), formSubmissionsRequestDTO);
        FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

        // Added - create commercial form Data
        accountEntityFound.setCommercialSubmissionData(formSubmissionData.getSubmissionData());

        accountEntityFound.setCommercialFormSubmissionId(formSubmissionData.getId());
        accountEntityFound.setIsDraft(false);
        return commercialEntityToCommercialNewResponseDTO(accountRepository.save(accountEntityFound));
    }

    private CommercialNewResponseDTO commercialEntityToCommercialNewResponseDTO(AccountNewEntity accountEntity) {
        CommercialNewResponseDTO commercialNewResponseDTO = new CommercialNewResponseDTO();
        commercialNewResponseDTO.setId(accountEntity.getId());
        commercialNewResponseDTO.setMsp(accountEntity.getMsp());
        commercialNewResponseDTO.setMarkUp(accountEntity.getMarkUp());
        commercialNewResponseDTO.setFormId(accountEntity.getCommercialFormId());

        // Get form submission data
        HttpResponse formSubmissionResponse = formSubmissionAPIClient.getFormSubmission(accountEntity.getCommercialFormSubmissionId());
        FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
        commercialNewResponseDTO.setSubmissionData(MappingUtil.convertJsonNodeToJSONString(formSubmissionData.getSubmissionData()));

        // Added - set commercial form data to the response
        commercialNewResponseDTO.setCommercialSubmissionData(formSubmissionData.getSubmissionData());
        return commercialNewResponseDTO;
    }

    private FormSubmissionsRequestDTO commercialRequestToFormSubmissionRequestDTO(CommercialNewRequest commercialNewRequest) {
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
        formSubmissionsRequestDTO.setUserId(getUserId());
        formSubmissionsRequestDTO.setFormId(commercialNewRequest.getFormId());
        formSubmissionsRequestDTO.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(commercialNewRequest.getFormData()));
        formSubmissionsRequestDTO.setEntityId(commercialNewRequest.getEntityId());
        formSubmissionsRequestDTO.setEntityType(commercialNewRequest.getEntityType());
        return formSubmissionsRequestDTO;
    }

    private Integer getUserId() {
        String email = JwtUtil.getEmailFromContext();
        HttpResponse userResponse = userAPIClient.getUserByEmail(email);
        UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
        return userData.getId();
    }
}
