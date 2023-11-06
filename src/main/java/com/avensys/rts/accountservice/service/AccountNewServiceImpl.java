package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.APIClient.*;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.entity.AccountNewEntity;
import com.avensys.rts.accountservice.exception.RequiredDocumentMissingException;
import com.avensys.rts.accountservice.model.FieldInformation;
import com.avensys.rts.accountservice.payloadnewrequest.AccountNewRequestDTO;
import com.avensys.rts.accountservice.payloadnewrequest.FormSubmissionsRequestDTO;
import com.avensys.rts.accountservice.payloadnewresponse.*;
import com.avensys.rts.accountservice.payloadrequest.DocumentRequestDTO;
import com.avensys.rts.accountservice.payloadresponse.*;
import com.avensys.rts.accountservice.repository.AccountNewRepository;
import com.avensys.rts.accountservice.util.JwtUtil;
import com.avensys.rts.accountservice.util.MappingUtil;
import com.avensys.rts.accountservice.util.StringUtil;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ContactAPIClient contactAPIClient;

    @Autowired
    private InstructionAPIClient instructionAPIClient;

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
    public AccountNewResponseDTO createAccount(AccountNewRequestDTO accountRequest) {
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
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = accountNewRequestDTOToFormSubmissionRequestDTO(savedAccountEntity, accountRequest);
        HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
        FormSubmissionsResponseDTO formSubmissionData = MappingUtil.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

        // Added - Save accountSubmissionData
        savedAccountEntity.setAccountSubmissionData(formSubmissionsRequestDTO.getSubmissionData());

        savedAccountEntity.setFormSubmissionId(formSubmissionData.getId());
        System.out.println("Form Submission Id: " + savedAccountEntity.getFormSubmissionId());
        return accountEntityToAccountResponseDTO(savedAccountEntity);
    }

    /**
     * Get account by id
     * @param id
     * @return
     */
    @Override
    public AccountNewResponseDTO getAccount(Integer id) {
        // Get account data from account microservice
        AccountNewEntity accountEntity = accountRepository.findByIdAndDeleted(id, false, true)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return accountEntityToAccountResponseDTO(accountEntity);
    }

    @Override
    public AccountNewResponseDTO getAccountIfDraft() {
        Optional<AccountNewEntity> accountEntity = accountRepository.findByUserAndDraftAndDeleted(getUserId(), true, false, true);
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
        AccountNewEntity accountEntity = accountRepository.findByIdAndDeleted(id, false, true)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Update account data
        accountEntity.setName(accountRequest.getAccountName());
        accountEntity.setUpdatedBy(getUserId());
        accountEntity.setFormId(accountRequest.getFormId());
        accountRepository.save(accountEntity);

        // Update form submission data
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = accountNewRequestDTOToFormSubmissionRequestDTO(accountEntity, accountRequest);
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

        // Added - Update accountSubmissionData
        accountEntity.setAccountSubmissionData(formSubmissionsRequestDTO.getSubmissionData());

        return accountEntityToAccountResponseDTO(accountEntity);
    }

    @Override
    public List<AccountNameReponseDTO> getAllAccountsName() {
        List<AccountNewEntity> accountEntities = accountRepository.findAllByUserAndDeleted(getUserId(), false, true);
        return accountEntities.stream().map(this::accountNewEntityToAccountNameResponseDTO).toList();
    }

    @Override
    public List<Map<String, String>> getAllAccountsFields() {
        List<AccountNewEntity> accountEntities = accountRepository.findAllByUserAndDeleted(getUserId(), false, true);
        if (accountEntities.isEmpty()) {
            return null;
        }

        // Declare a new haspmap to store the label and value
        Map<String, String> keyMap = new HashMap<>();

        // Lets store normal column first
        keyMap.put("Account Number", "account_number");
        keyMap.put("Created At", "createdAt");
        keyMap.put("Updated At", "updatedAt");
        keyMap.put("Created By", "createdByName");
        keyMap.put("Updated By", "updatedByName");
        // Loop through the account submission data jsonNode
        for (AccountNewEntity accountNewEntity : accountEntities) {
            if (accountNewEntity.getAccountSubmissionData() != null) {
                Iterator<String> accountFieldNames = accountNewEntity.getAccountSubmissionData().fieldNames();
                while (accountFieldNames.hasNext()) {
                    String fieldName = accountFieldNames.next();
                    keyMap.put(StringUtil.convertCamelCaseToTitleCase2(fieldName), "accountSubmissionData." + fieldName);
                }
            }

            if (accountNewEntity.getCommercialSubmissionData() != null) {
                Iterator<String> commercialFieldNames = accountNewEntity.getCommercialSubmissionData().fieldNames();
                while (commercialFieldNames.hasNext()) {
                    String fieldName = commercialFieldNames.next();
                    keyMap.put(StringUtil.convertCamelCaseToTitleCase2(fieldName), "commercialSubmissionData." + fieldName);
                }
            }
        }

        List<Map<String, String>> fieldOptions = new ArrayList<>();
        // Loop Through map
        for (Map.Entry<String, String> entry : keyMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
            // Creat a list of map with label and value
            Map<String, String> map = new HashMap<>();
            map.put("label", entry.getKey());
            map.put("value", entry.getValue());
            if (entry.getValue().contains(".")) {
                String[] split = entry.getValue().split("\\.");
                map.put("sortValue", StringUtil.camelCaseToSnakeCase(split[0]) + "." + split[1]);
            } else {
                map.put("sortValue", StringUtil.camelCaseToSnakeCase(entry.getValue()));
            }
            fieldOptions.add(map);
        }
        return fieldOptions;
    }

    @Override
    public Set<FieldInformation> getAllAccountsFieldsNew() {
        List<AccountNewEntity> accountEntities = accountRepository.findAllByUserAndDeleted(getUserId(), false, true);
        if (accountEntities.isEmpty()) {
            return null;
        }

        // Declare a new haspmap to store the label and value
        Set<FieldInformation> fieldColumn = new HashSet<>();
        fieldColumn.add(new FieldInformation("Account Number", "accountNumber", true, "account_number"));
        fieldColumn.add(new FieldInformation("Created At", "createdAt", true, "created_at"));
        fieldColumn.add(new FieldInformation("Updated At", "updatedAt", true, "updated_at"));
        fieldColumn.add(new FieldInformation("Created By", "createdByName", false, null));

        // Loop through the account submission data jsonNode
        for (AccountNewEntity accountNewEntity : accountEntities) {
            if (accountNewEntity.getAccountSubmissionData() != null) {
                Iterator<String> accountFieldNames = accountNewEntity.getAccountSubmissionData().fieldNames();
                while (accountFieldNames.hasNext()) {
                    String fieldName = accountFieldNames.next();
                    fieldColumn.add(new FieldInformation(StringUtil.convertCamelCaseToTitleCase2(fieldName), "accountSubmissionData." + fieldName, true, "account_submission_data." + fieldName));
                }
            }

            if (accountNewEntity.getCommercialSubmissionData() != null) {
                Iterator<String> commercialFieldNames = accountNewEntity.getCommercialSubmissionData().fieldNames();
                while (commercialFieldNames.hasNext()) {
                    String fieldName = commercialFieldNames.next();
                    fieldColumn.add(new FieldInformation(StringUtil.convertCamelCaseToTitleCase2(fieldName), "commercialSubmissionData." + fieldName, true, "commercial_submission_data." + fieldName));
                }
            }
        }
        return fieldColumn;
    }

    @Override
    public AccountListingNewResponseDTO getAccountListingPage(Integer page, Integer size, String sortBy, String sortDirection) {
        // Get sort direction
        Sort.Direction direction = Sort.DEFAULT_DIRECTION;
        if (sortDirection != null && !sortDirection.isEmpty()) {
            direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        }
        if (sortBy == null || sortBy.isEmpty() || sortBy.equals("")) {
            sortBy = "updated_at";
            direction = Sort.Direction.DESC;
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<AccountNewEntity> accountEntitiesPage = null;
        // Try with numeric first else try with string (jsonb)
        try {
            accountEntitiesPage = accountRepository.findAllByOrderByNumeric(
                    getUserId(),
                    false,
                    false,
                    true,
                    pageRequest
            );
        } catch (Exception e) {
            accountEntitiesPage = accountRepository.findAllByOrderByString(
                    getUserId(),
                    false,
                    false,
                    true,
                    pageRequest
            );
        }

        return pageAccountListingToAccountListingResponseDTO(accountEntitiesPage);
    }

    @Override
    public AccountListingNewResponseDTO getAccountListingPageWithSearch(Integer page, Integer size, String sortBy, String sortDirection, String searchTerm, List<String> searchFields) {
        // Get sort direction
        Sort.Direction direction = Sort.DEFAULT_DIRECTION;
        if (sortDirection != null) {
            direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        }
        if (sortBy == null) {
            sortBy = "updated_at";
            direction = Sort.Direction.DESC;
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<AccountNewEntity> accountEntitiesPage = null;
        // Try with numeric first else try with string (jsonb)
        try {
            accountEntitiesPage = accountRepository.findAllByOrderByAndSearchNumeric(
                    getUserId(),
                    false,
                    false,
                    true,
                    pageRequest,
                    searchFields,
                    searchTerm
            );
        } catch (Exception e) {
            accountEntitiesPage = accountRepository.findAllByOrderByAndSearchString(
                    getUserId(),
                    false,
                    false,
                    true,
                    pageRequest,
                    searchFields,
                    searchTerm
            );
        }
        return pageAccountListingToAccountListingResponseDTO(accountEntitiesPage);
    }

    @Override
    @Transactional
    public void deleteDraftAccount(Integer accountId) {
        // Get account which is in draft state.
        AccountNewEntity accountEntityFound = accountRepository.findByIdAndDraft(accountId, true, true).orElseThrow(
                () -> new RuntimeException("Account not found")
        );

        // Delete all contacts belong to this account
        HttpResponse contactResponse = contactAPIClient.deleteContactsByEntityTypeAndEntityId("account_contact", accountId);
        // Delete all Documents
        HttpResponse documentResponse = documentAPIClient.deleteDocumentsByEntityTypeAndEntityId("account_document", accountId);
        // Delete account client instructions
        HttpResponse instructionResponse = instructionAPIClient.deleteInstructionByEntityId("account_instruction", accountId);
        // Delete all client instruction documents
        HttpResponse instructionDocumentResponse = documentAPIClient.deleteDocumentsByEntityTypeAndEntityId("account_instruction_document", accountId);

        // Delete all the commercial form submission if it exist
        if (accountEntityFound.getCommercialFormSubmissionId() != null) {
            HttpResponse commercialFormSubmissionResponse = formSubmissionAPIClient.deleteFormSubmission(accountEntityFound.getCommercialFormSubmissionId());
        }

        // Delete all account form submission
        if (accountEntityFound.getFormSubmissionId() != null) {
            HttpResponse formSubmissionResponse = formSubmissionAPIClient.deleteFormSubmission(accountEntityFound.getFormSubmissionId());
        }

        // Delete account required document
        HttpResponse requiredDocumentResponse = documentAPIClient.deleteDocumentsByEntityTypeAndEntityId("account_account", accountId);

        // Check if there is parent company and delete it
        accountEntityFound.setParentCompany(null);
        accountRepository.delete(accountEntityFound);
        System.out.println("Draft account deleted");
    }

    @Override
    public void softDeleteAccount(Integer accountId) {
        AccountNewEntity accountEntityFound = accountRepository.findByIdAndDeleted(accountId, false, true).orElseThrow(
                () -> new RuntimeException("Account not found")
        );

        // Soft delete the account
        accountEntityFound.setIsDeleted(true);

        // Save account
        accountRepository.save(accountEntityFound);
    }

    @Override
    public List<AccountNewEntity> getAllAccountsNameWithSearch(String query) {
        // Regex to get the field name, operator and value
//        String regex = "(\\w+)([><]=?|!=|=)(\\w+)";
//
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(query);
//
//        while (matcher.find()) {
//            String fieldName = matcher.group(1);
//            String operator = matcher.group(2);
//            String value = matcher.group(3);
//
//            // Now you have fieldName, operator, and value for each key-value pair
//            System.out.println("Field Name: " + fieldName);
//            System.out.println("Operator: " + operator);
//            System.out.println("Value: " + value);
//        }
        List<AccountNewEntity> accountEntities = accountRepository.getAllAccountsNameWithSearch(query, getUserId(), false, false);
        return accountEntities;
    }

    @Override
    public List<AccountNewEntity> getAllAccountsByUser(boolean draft, boolean deleted) {
        List<AccountNewEntity> accountEntities = accountRepository.findAllByUserAndDraftAndDeleted(getUserId(), draft, deleted, true);
        return accountEntities;
    }

    /**
     * Page to account listing new response
     */
    private AccountListingNewResponseDTO pageAccountListingToAccountListingResponseDTO(Page<AccountNewEntity> accountNewEntitiesPage) {
        AccountListingNewResponseDTO accountListingResponseDTO = new AccountListingNewResponseDTO();
        accountListingResponseDTO.setTotalElements(accountNewEntitiesPage.getTotalElements());
        accountListingResponseDTO.setTotalPages(accountNewEntitiesPage.getTotalPages());
        accountListingResponseDTO.setPage(accountNewEntitiesPage.getNumber());
        accountListingResponseDTO.setPageSize(accountNewEntitiesPage.getSize());
        List<AccountNewListingDataDTO> accountNewListingDataDTOS = new ArrayList<>();
        accountNewListingDataDTOS = accountNewEntitiesPage.getContent().stream().map(
                accountNewEntity -> {
                    AccountNewListingDataDTO accountNewListingDataDTO = new AccountNewListingDataDTO(accountNewEntity);
                    // Get created by User data from user microservice
                    HttpResponse createUserResponse = userAPIClient.getUserById(accountNewEntity.getCreatedBy());
                    UserResponseDTO createUserData = MappingUtil.mapClientBodyToClass(createUserResponse.getData(), UserResponseDTO.class);
                    accountNewListingDataDTO.setCreatedByName(createUserData.getFirstName() + " " + createUserData.getLastName());
                    HttpResponse updateUserResponse = userAPIClient.getUserById(accountNewEntity.getUpdatedBy());
                    UserResponseDTO updateUserData = MappingUtil.mapClientBodyToClass(updateUserResponse.getData(), UserResponseDTO.class);
                    accountNewListingDataDTO.setUpdatedByName(updateUserData.getFirstName() + " " + updateUserData.getLastName());
                    return accountNewListingDataDTO;
                }
        ).toList();

        accountListingResponseDTO.setAccounts(accountNewListingDataDTOS);
        return accountListingResponseDTO;
    }


    /**
     * Internal method to convert AccountEntity to AccountNameReponseDTO
     *
     * @param accountEntity
     * @return
     */
    private AccountNameReponseDTO accountNewEntityToAccountNameResponseDTO(AccountNewEntity accountEntity) {
        AccountNameReponseDTO accountNameReponseDTO = new AccountNameReponseDTO();
        accountNameReponseDTO.setId(accountEntity.getId());
        accountNameReponseDTO.setName(accountEntity.getName());
        return accountNameReponseDTO;
    }

    private FormSubmissionsRequestDTO accountNewRequestDTOToFormSubmissionRequestDTO(AccountNewEntity accountNewEntity, AccountNewRequestDTO accountNewRequestDTO) {
        FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
        formSubmissionsRequestDTO.setUserId(getUserId());
        formSubmissionsRequestDTO.setFormId(accountNewRequestDTO.getFormId());
        formSubmissionsRequestDTO.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(accountNewRequestDTO.getFormData()));
        formSubmissionsRequestDTO.setEntityId(accountNewEntity.getId());
        formSubmissionsRequestDTO.setEntityType(ACCOUNT_TYPE);
        return formSubmissionsRequestDTO;
    }

    private AccountNewResponseDTO accountEntityToAccountResponseDTO(AccountNewEntity accountEntity) {
        AccountNewResponseDTO accountResponseDTO = new AccountNewResponseDTO();
        accountResponseDTO.setId(accountEntity.getId());
        accountResponseDTO.setName(accountEntity.getName());
        accountResponseDTO.setFormId(accountEntity.getFormId());
        accountResponseDTO.setCreatedAt(accountEntity.getCreatedAt());
        accountResponseDTO.setUpdatedAt(accountEntity.getUpdatedAt());
        accountResponseDTO.setAccountCountry(accountEntity.getAccountCountry());

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
        accountResponseDTO.setSubmissionData(MappingUtil.convertJsonNodeToJSONString(formSubmissionData.getSubmissionData()));

        // Added - Get accountSubmissionData and add to response
        accountResponseDTO.setAccountSubmissionData(formSubmissionData.getSubmissionData());

        return accountResponseDTO;
    }

    private AccountNewEntity accountRequestDTOToAccountEntity(AccountNewRequestDTO accountRequest) {
        AccountNewEntity accountEntity = new AccountNewEntity();
        accountEntity.setName(accountRequest.getAccountName());
        accountEntity.setAccountNumber("A" + RandomStringUtils.randomNumeric(7));
        accountEntity.setIsDraft(true);
        accountEntity.setIsDeleted(false);
        accountEntity.setCreatedBy(getUserId());
        accountEntity.setUpdatedBy(getUserId());
        accountEntity.setFormId(accountRequest.getFormId());
        accountEntity.setAccountCountry(accountRequest.getAccountCountry());
        return accountRepository.save(accountEntity);
    }

    private Integer getUserId() {
        String email = JwtUtil.getEmailFromContext();
        HttpResponse userResponse = userAPIClient.getUserByEmail(email);
        UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
        return userData.getId();
    }
}
