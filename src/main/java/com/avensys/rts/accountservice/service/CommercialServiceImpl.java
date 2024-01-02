package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.APIClient.FormSubmissionAPIClient;
import com.avensys.rts.accountservice.APIClient.UserAPIClient;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.payloadnewrequest.CommercialRequest;
import com.avensys.rts.accountservice.payloadnewrequest.FormSubmissionsRequestDTO;
import com.avensys.rts.accountservice.payloadnewresponse.CommercialResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.FormSubmissionsResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.UserResponseDTO;
import com.avensys.rts.accountservice.repository.AccountRepository;
import com.avensys.rts.accountservice.util.JwtUtil;
import com.avensys.rts.accountservice.util.MappingUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommercialServiceImpl implements CommercialService{

    private final AccountRepository accountRepository;

    @Autowired
    private FormSubmissionAPIClient formSubmissionAPIClient;

    @Autowired
    private UserAPIClient userAPIClient;

    public CommercialServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public CommercialResponseDTO createCommercial(Integer id, CommercialRequest commercialRequest) {
        AccountEntity accountEntityFound = accountRepository.findByIdAndDeleted(id, false, true)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountEntityFound.setMarkUp(commercialRequest.getMarkUp());
        accountEntityFound.setMsp(commercialRequest.getMsp());
        accountEntityFound.setCommercialFormId(commercialRequest.getFormId());

        // Save form data to form submission microservice
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = commercialRequestToFormSubmissionRequestDTO(
                commercialRequest);
        HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
        FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

        // Added - create commercial form Data
        accountEntityFound.setCommercialSubmissionData(formSubmissionData.getSubmissionData());

        accountEntityFound.setCommercialFormSubmissionId(formSubmissionData.getId());
        accountEntityFound.setIsDraft(false);
        return commercialEntityToCommercialNewResponseDTO(accountRepository.save(accountEntityFound));
    }

    @Override
    public CommercialResponseDTO getCommercial(Integer id) {
        Optional<AccountEntity> accountEntity = accountRepository.findByIdAndDeleted(id, false, true);
        if (accountEntity.isPresent()) {
            return commercialEntityToCommercialNewResponseDTO(accountEntity.get());
        }
        return null;
    }

    @Override
    public CommercialResponseDTO updateCommercial(Integer id, CommercialRequest commercialRequest) {
        AccountEntity accountEntityFound = accountRepository.findByIdAndDeleted(id, false, true)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountEntityFound.setMarkUp(commercialRequest.getMarkUp());
        accountEntityFound.setMsp(commercialRequest.getMsp());
        accountEntityFound.setCommercialFormId(commercialRequest.getFormId());

        // Update form data to form submission microservice
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = commercialRequestToFormSubmissionRequestDTO(
                commercialRequest);
        HttpResponse formSubmissionResponse = formSubmissionAPIClient.updateFormSubmission(accountEntityFound.getCommercialFormSubmissionId(), formSubmissionsRequestDTO);
        FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

        // Added - create commercial form Data
        accountEntityFound.setCommercialSubmissionData(formSubmissionData.getSubmissionData());

        accountEntityFound.setCommercialFormSubmissionId(formSubmissionData.getId());
        accountEntityFound.setIsDraft(false);
        return commercialEntityToCommercialNewResponseDTO(accountRepository.save(accountEntityFound));
    }

    private CommercialResponseDTO commercialEntityToCommercialNewResponseDTO(AccountEntity accountEntity) {
        CommercialResponseDTO commercialNewResponseDTO = new CommercialResponseDTO();
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

    private FormSubmissionsRequestDTO commercialRequestToFormSubmissionRequestDTO(CommercialRequest commercialRequest) {
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
        formSubmissionsRequestDTO.setUserId(getUserId());
        formSubmissionsRequestDTO.setFormId(commercialRequest.getFormId());
        formSubmissionsRequestDTO.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(commercialRequest.getFormData()));
        formSubmissionsRequestDTO.setEntityId(commercialRequest.getEntityId());
        formSubmissionsRequestDTO.setEntityType(commercialRequest.getEntityType());
        return formSubmissionsRequestDTO;
    }

    private Integer getUserId() {
        String email = JwtUtil.getEmailFromContext();
        HttpResponse userResponse = userAPIClient.getUserByEmail(email);
        UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
        return userData.getId();
    }
}
