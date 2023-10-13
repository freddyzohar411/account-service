package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.APIClient.AddressAPIClient;
import com.avensys.rts.accountservice.APIClient.DocumentAPIClient;
import com.avensys.rts.accountservice.APIClient.FormSubmissionAPIClient;
import com.avensys.rts.accountservice.APIClient.UserAPIClient;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.entity.AccountNewEntity;
import com.avensys.rts.accountservice.exception.RequiredDocumentMissingException;
import com.avensys.rts.accountservice.payloadnewrequest.AccountNewRequestDTO;
import com.avensys.rts.accountservice.payloadnewrequest.CommercialNewRequest;
import com.avensys.rts.accountservice.payloadnewrequest.FormSubmissionsRequestDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNewResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.CommercialNewResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.FormSubmissionsResponseDTO;
import com.avensys.rts.accountservice.payloadrequest.AccountRequestDTO;
import com.avensys.rts.accountservice.payloadrequest.DocumentRequestDTO;
import com.avensys.rts.accountservice.payloadresponse.AccountResponseDTO;
import com.avensys.rts.accountservice.payloadresponse.DocumentResponseDTO;
import com.avensys.rts.accountservice.payloadresponse.UserResponseDTO;
import com.avensys.rts.accountservice.repository.AccountNewRepository;
import com.avensys.rts.accountservice.repository.AccountRepository;
import com.avensys.rts.accountservice.util.JwtUtil;
import com.avensys.rts.accountservice.util.MappingUtil;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountNewServiceImpl implements AccountNewService {

    private final String ACTIVE_STATUS = "active";
    private final String ACCOUNT_TYPE = "account_account";
    private final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private final AccountNewRepository accountRepository;

    @Autowired
    private AddressAPIClient addressAPIClient;

    @Autowired
    private DocumentAPIClient documentAPIClient;

    @Autowired
    private UserAPIClient userAPIClient;

    @Autowired
    private FormSubmissionAPIClient formSubmissionAPIClient;

    public AccountNewServiceImpl(AccountNewRepository accountRepository, AddressAPIClient addressAPIClient, DocumentAPIClient documentAPIClient, UserAPIClient userAPIClient, FormSubmissionAPIClient formSubmissionAPIClient) {
        this.accountRepository = accountRepository;
        this.addressAPIClient = addressAPIClient;
        this.documentAPIClient = documentAPIClient;
        this.userAPIClient = userAPIClient;
        this.formSubmissionAPIClient = formSubmissionAPIClient;
    }

    /**
     * Create an account draft
     *
     * @param accountRequest
     * @return
     */
    @Override
    @Transactional
    public AccountResponseDTO createAccount(AccountNewRequestDTO accountRequest) {
        System.out.println("Account create: Service");
        System.out.println(accountRequest);
        AccountNewEntity savedAccountEntity = accountRequestDTOToAccountEntity(accountRequest);

        System.out.println("Account Id: " + savedAccountEntity.getId());

        // Save Document to document microservice
        if (accountRequest.getUploadAgreement() != null) {
            DocumentRequestDTO documentRequestDTO = new DocumentRequestDTO();
            // Save document and tag to account entity
            documentRequestDTO.setEntityId(savedAccountEntity.getId());
            documentRequestDTO.setEntityType(ACCOUNT_TYPE);

            documentRequestDTO.setFile(accountRequest.getUploadAgreement());
            HttpResponse documentResponse = documentAPIClient.createDocument(documentRequestDTO);
            DocumentResponseDTO documentData = MappingUtil.mapClientBodyToClass(documentResponse.getData(), DocumentResponseDTO.class);
        } else {
            throw new RequiredDocumentMissingException("Upload agreement document is required");
        }

        // Save form data to form submission microservice
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
        formSubmissionsRequestDTO.setFormId(accountRequest.getFormId());
        formSubmissionsRequestDTO.setSubmissionData(accountRequest.getFormData());
        formSubmissionsRequestDTO.setEntityId(savedAccountEntity.getId());
        formSubmissionsRequestDTO.setEntityType(ACCOUNT_TYPE);
        HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
        FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

        savedAccountEntity.setFormSubmissionId(formSubmissionData.getId());
        System.out.println("Form Submission Id: " + savedAccountEntity.getFormSubmissionId());
        return null;
    }

    /**
     * Get account by id
     *
     * @param id
     * @return
     */
    @Override
    public AccountNewResponseDTO getAccount(Integer id) {
        // Get account data from account microservice
        AccountNewEntity accountEntity = accountRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return accountEntityToAccountResponseDTO(accountEntity);
    }

    @Override
    public AccountNewResponseDTO getAccountIfDraft() {
        Optional<AccountNewEntity> accountEntity = accountRepository.findByUserAndDraftAndDeleted(getUserId(), true, false);
        if (accountEntity.isPresent()) {
            return accountEntityToAccountResponseDTO(accountEntity.get());
        }
        return null;
    }

    @Override
    @Transactional
    public AccountNewResponseDTO updateAccount(Integer id, AccountNewRequestDTO accountRequest) {
        System.out.println("Account update: Service");
        System.out.println("Update account Id: " + id);
        System.out.println(accountRequest);

        // Get account data from account microservice
        AccountNewEntity accountEntity = accountRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Update account data
        accountEntity.setName(accountRequest.getAccountName());
        accountEntity.setUpdatedBy(getUserId());
        accountEntity.setFormId(accountRequest.getFormId());
        accountRepository.save(accountEntity);

        // Update form submission data
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
        formSubmissionsRequestDTO.setFormId(accountRequest.getFormId());
        formSubmissionsRequestDTO.setSubmissionData(accountRequest.getFormData());
        formSubmissionsRequestDTO.setEntityId(accountEntity.getId());
        formSubmissionsRequestDTO.setEntityType(ACCOUNT_TYPE);
        HttpResponse formSubmissionResponse = formSubmissionAPIClient.updateFormSubmission(accountEntity.getFormSubmissionId(), formSubmissionsRequestDTO);
        FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

        // Update document data
        if (accountRequest.getUploadAgreement() != null) {
            DocumentRequestDTO documentRequestDTO = new DocumentRequestDTO();
            // Save document and tag to account entity
            documentRequestDTO.setEntityId(accountEntity.getId());
            documentRequestDTO.setEntityType(ACCOUNT_TYPE);

            documentRequestDTO.setFile(accountRequest.getUploadAgreement());
            HttpResponse documentResponse = documentAPIClient.updateDocument(documentRequestDTO);
            DocumentResponseDTO documentData = MappingUtil.mapClientBodyToClass(documentResponse.getData(), DocumentResponseDTO.class);
        }

        return accountEntityToAccountResponseDTO(accountEntity);
    }

    @Override
    @Transactional
    public CommercialNewResponseDTO createCommercial(Integer id, CommercialNewRequest commercialNewRequest) {
        AccountNewEntity accountEntityFound = accountRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountEntityFound.setMarkUp(commercialNewRequest.getMarkUp());
        accountEntityFound.setMsp(commercialNewRequest.getMsp());
        accountEntityFound.setCommercialFormId(commercialNewRequest.getFormId());

        // Save form data to form submission microservice
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = commercialRequestToFormSubmissionRequestDTO(commercialNewRequest);
        HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
        FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

        accountEntityFound.setCommercialFormSubmissionId(formSubmissionData.getId());
        accountEntityFound.setDraft(false);
        return commercialEntityToCommercialNewResponseDTO(accountRepository.save(accountEntityFound));
    }

    @Override
    public CommercialNewResponseDTO getCommercial(Integer id) {
        Optional<AccountNewEntity> accountEntity = accountRepository.findByIdAndDeleted(id, false);
        if (accountEntity.isPresent()) {
            return commercialEntityToCommercialNewResponseDTO(accountEntity.get());
        }
        return null;
    }

    @Override
    public CommercialNewResponseDTO updateCommercial(Integer id, CommercialNewRequest commercialNewRequest) {
        AccountNewEntity accountEntityFound = accountRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountEntityFound.setMarkUp(commercialNewRequest.getMarkUp());
        accountEntityFound.setMsp(commercialNewRequest.getMsp());
        accountEntityFound.setCommercialFormId(commercialNewRequest.getFormId());

        // Update form data to form submission microservice
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = commercialRequestToFormSubmissionRequestDTO(commercialNewRequest);
        HttpResponse formSubmissionResponse = formSubmissionAPIClient.updateFormSubmission(accountEntityFound.getCommercialFormSubmissionId(), formSubmissionsRequestDTO);
        FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

        accountEntityFound.setCommercialFormSubmissionId(formSubmissionData.getId());
        accountEntityFound.setDraft(false);
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
        commercialNewResponseDTO.setSubmissionData(formSubmissionData.getSubmissionData());
        return commercialNewResponseDTO;
    }

    private FormSubmissionsRequestDTO commercialRequestToFormSubmissionRequestDTO(CommercialNewRequest commercialNewRequest) {
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
        formSubmissionsRequestDTO.setFormId(commercialNewRequest.getFormId());
        formSubmissionsRequestDTO.setSubmissionData(commercialNewRequest.getFormData());
        formSubmissionsRequestDTO.setEntityId(commercialNewRequest.getEntityId());
        formSubmissionsRequestDTO.setEntityType(commercialNewRequest.getEntityType());
        return formSubmissionsRequestDTO;
    }

    private AccountNewResponseDTO accountEntityToAccountResponseDTO(AccountNewEntity accountEntity) {
        AccountNewResponseDTO accountResponseDTO = new AccountNewResponseDTO();
        accountResponseDTO.setId(accountEntity.getId());
        accountResponseDTO.setName(accountEntity.getName());
        accountResponseDTO.setFormId(accountEntity.getFormId());
        accountResponseDTO.setCreatedAt(accountEntity.getCreatedAt());
        accountResponseDTO.setUpdatedAt(accountEntity.getUpdatedAt());

        // Get created by User data from user microservice
        HttpResponse userResponse = userAPIClient.getUserById(accountEntity.getCreatedBy());
        UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
        accountResponseDTO.setCreatedBy(userData.getFirstName() + " " + userData.getLastName());

        // Get updated by user data from user microservice
        if (accountEntity.getUpdatedBy() == accountEntity.getCreatedBy()) {
            accountResponseDTO.setUpdatedBy(userData.getFirstName() + " " + userData.getLastName());
        } else {
            HttpResponse updatedByUserResponse = userAPIClient.getUserById(accountEntity.getUpdatedBy());
            UserResponseDTO updatedByUserData = MappingUtil.mapClientBodyToClass(updatedByUserResponse.getData(), UserResponseDTO.class);
            accountResponseDTO.setUpdatedBy(updatedByUserData.getFirstName() + " " + updatedByUserData.getLastName());
        }

        // Get form submission data
        HttpResponse formSubmissionResponse = formSubmissionAPIClient.getFormSubmission(accountEntity.getFormSubmissionId());
        FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
        accountResponseDTO.setSubmissionData(formSubmissionData.getSubmissionData());

        return accountResponseDTO;
    }

    private AccountNewEntity accountRequestDTOToAccountEntity(AccountNewRequestDTO accountRequest) {
        AccountNewEntity accountEntity = new AccountNewEntity();
        accountEntity.setName(accountRequest.getAccountName());
        System.out.println("ACCOUNT NAME: " + accountRequest.getAccountName());
        accountEntity.setAccountNumber("A" + RandomStringUtils.randomNumeric(7));
        accountEntity.setDraft(true);
        accountEntity.setDeleted(false);
        accountEntity.setCreatedBy(getUserId());
        accountEntity.setUpdatedBy(getUserId());
        accountEntity.setFormId(accountRequest.getFormId());
        return accountRepository.save(accountEntity);
    }

    private Integer getUserId() {
        String email = JwtUtil.getEmailFromContext();
        HttpResponse userResponse = userAPIClient.getUserByEmail(email);
        UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
        return userData.getId();
    }
}
