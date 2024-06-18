package com.avensys.rts.accountservice.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.avensys.rts.accountservice.payloadnewrequest.*;
import com.avensys.rts.accountservice.util.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.avensys.rts.accountservice.APIClient.ContactAPIClient;
import com.avensys.rts.accountservice.APIClient.DocumentAPIClient;
import com.avensys.rts.accountservice.APIClient.FormSubmissionAPIClient;
import com.avensys.rts.accountservice.APIClient.InstructionAPIClient;
import com.avensys.rts.accountservice.APIClient.UserAPIClient;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.entity.CustomFieldsEntity;
import com.avensys.rts.accountservice.exception.DuplicateResourceException;
import com.avensys.rts.accountservice.model.AccountExtraData;
import com.avensys.rts.accountservice.model.FieldInformation;
import com.avensys.rts.accountservice.payloadnewresponse.AccountListingDataDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountListingResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNameResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNewResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.CustomFieldsResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.DocumentResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.FormSubmissionsResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.UserResponseDTO;
import com.avensys.rts.accountservice.repository.AccountCustomFieldsRepository;
import com.avensys.rts.accountservice.repository.AccountRepository;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.transaction.Transactional;

@Service
public class AccountServiceImpl implements AccountService {

	private final String ACCOUNT_TYPE = "account_account";

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountCustomFieldsRepository accountCustomFieldsRepository;

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

	@Autowired
	private UserUtil userUtil;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Create an account draft
	 *
	 * @param accountRequest
	 * @return
	 */
	@Override
	@Transactional
	public AccountNewResponseDTO createAccount(AccountRequestDTO accountRequest) {
		System.out.println("Account create: Service");
		System.out.println(accountRequest);
		AccountEntity savedAccountEntity = accountRequestDTOToAccountEntity(accountRequest);

		System.out.println("Account Id: " + savedAccountEntity.getId());

		// Save Document to document microservice
		if (accountRequest.getUploadAgreement() != null) {
			CommercialRequest.DocumentRequestDTO documentRequestDTO = new CommercialRequest.DocumentRequestDTO();
			// Save document and tag to account entity
			documentRequestDTO.setEntityId(savedAccountEntity.getId());
			documentRequestDTO.setEntityType(ACCOUNT_TYPE);

			documentRequestDTO.setFile(accountRequest.getUploadAgreement());
			HttpResponse documentResponse = documentAPIClient.createDocument(documentRequestDTO);
			DocumentResponseDTO documentData = MappingUtil.mapClientBodyToClass(documentResponse.getData(),
					DocumentResponseDTO.class);
		}

		// Save form data to form submission microservice
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = accountNewRequestDTOToFormSubmissionRequestDTO(
				savedAccountEntity, accountRequest);
		HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		// Added - Save accountSubmissionData
		savedAccountEntity.setAccountSubmissionData(formSubmissionsRequestDTO.getSubmissionData());

		savedAccountEntity.setFormSubmissionId(formSubmissionData.getId());

		System.out.println("Form Submission Id: " + savedAccountEntity.getFormSubmissionId());
		return accountEntityToAccountResponseDTO(savedAccountEntity);
	}

	@Override
	public List<CustomFieldsEntity> getAllCreatedCustomViews() {

		List<CustomFieldsEntity> customfields = accountCustomFieldsRepository.findAllByUser(getUserId(), "Account",
				false);
		return customfields;
	}

	@Override
	public CustomFieldsResponseDTO updateCustomView(Long id) {
		if (accountCustomFieldsRepository.findById(id).get().getIsDeleted()) {
			throw new DuplicateResourceException(
					messageSource.getMessage("error.customViewAlreadyDeleted", null, LocaleContextHolder.getLocale()));
		}

		List<CustomFieldsEntity> selectedCustomView = accountCustomFieldsRepository.findAllByUser(getUserId(),
				"Account", false);
		for (CustomFieldsEntity customView : selectedCustomView) {
			if (customView.isSelected() == true) {
				customView.setSelected(false);
				accountCustomFieldsRepository.save(customView);
			}
		}
		Optional<CustomFieldsEntity> customFieldsEntity = accountCustomFieldsRepository.findById(id);
		customFieldsEntity.get().setSelected(true);
		accountCustomFieldsRepository.save(customFieldsEntity.get());

		return customFieldsEntityToCustomFieldsResponseDTO(customFieldsEntity.get());

	}

	@Override
	public CustomFieldsResponseDTO saveCustomFields(CustomFieldsRequestDTO customFieldsRequestDTO) {

		System.out.println(" Save Account customFields : Service");
		System.out.println(customFieldsRequestDTO);

		if (accountCustomFieldsRepository.existsByName(customFieldsRequestDTO.getName())) {
			throw new DuplicateResourceException(
					messageSource.getMessage("error.customViewNametaken", null, LocaleContextHolder.getLocale()));
		}

		List<CustomFieldsEntity> selectedCustomView = accountCustomFieldsRepository.findAllByUser(getUserId(),
				"Account", false);

		if (selectedCustomView != null) {
			for (CustomFieldsEntity customView : selectedCustomView) {
				if (customView.isSelected() == true) {
					customView.setSelected(false);
					accountCustomFieldsRepository.save(customView);
				}
			}

		}
		CustomFieldsEntity accountCustomFieldsEntity = customFieldsRequestDTOToCustomFieldsEntity(
				customFieldsRequestDTO);
		return customFieldsEntityToCustomFieldsResponseDTO(accountCustomFieldsEntity);
	}

	CustomFieldsEntity customFieldsRequestDTOToCustomFieldsEntity(CustomFieldsRequestDTO customFieldsRequestDTO) {
		CustomFieldsEntity customFieldsEntity = new CustomFieldsEntity();
		customFieldsEntity.setName(customFieldsRequestDTO.getName());
		customFieldsEntity.setType(customFieldsRequestDTO.getType());
		customFieldsEntity.setSelected(true);

		// converting list of string to comma saparated string
		String columnNames = String.join(",", customFieldsRequestDTO.getColumnName());
		customFieldsEntity.setColumnName(columnNames);
		// customFieldsEntity.setColumnName(MappingUtil.convertJsonNodeToJSONString(customFieldsRequestDTO.getColumnName()));
		customFieldsEntity.setCreatedBy(getUserId());
		customFieldsEntity.setUpdatedBy(getUserId());
		// Get Filters
		List<FilterDTO> filters = customFieldsRequestDTO.getFilters();
		if (filters != null) {
			customFieldsEntity.setFilters(JSONUtil.convertObjectToJsonNode(filters));
		}
		return accountCustomFieldsRepository.save(customFieldsEntity);
	}

	CustomFieldsResponseDTO customFieldsEntityToCustomFieldsResponseDTO(CustomFieldsEntity accountCustomFieldsEntity) {
		CustomFieldsResponseDTO customFieldsResponseDTO = new CustomFieldsResponseDTO();
		// Converting String to List of String.
		String columnNames = accountCustomFieldsEntity.getColumnName();
		List<String> columnNamesList = Arrays.asList(columnNames.split("\\s*,\\s*"));
		customFieldsResponseDTO.setColumnName(columnNamesList);
		customFieldsResponseDTO.setCreatedBy(accountCustomFieldsEntity.getCreatedBy());
		customFieldsResponseDTO.setName(accountCustomFieldsEntity.getName());
		customFieldsResponseDTO.setType(accountCustomFieldsEntity.getType());
		customFieldsResponseDTO.setUpdatedBy(accountCustomFieldsEntity.getUpdatedBy());
		customFieldsResponseDTO.setId(accountCustomFieldsEntity.getId());
		return customFieldsResponseDTO;
	}

	@Override
	public void softDelete(Long id) {
		CustomFieldsEntity customFieldsEntity = accountCustomFieldsRepository.findByIdAndDeleted(id, false, true)
				.orElseThrow(() -> new RuntimeException("Custom view not found"));

		// Soft delete the custom view
		customFieldsEntity.setIsDeleted(true);
		customFieldsEntity.setSelected(false);

		// Save custom view
		accountCustomFieldsRepository.save(customFieldsEntity);
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
		AccountEntity accountEntity = accountRepository.findByIdAndDeleted(id, false, true)
				.orElseThrow(() -> new RuntimeException("Account not found"));
		return accountEntityToAccountResponseDTO(accountEntity);
	}

	@Override
	public AccountNewResponseDTO getAccountIfDraft() {
		Optional<AccountEntity> accountEntity = accountRepository.findByUserAndDraftAndDeleted(getUserId(), true, false,
				true);
		if (accountEntity.isPresent()) {
			return accountEntityToAccountResponseDTO(accountEntity.get());
		}
		return null;
	}

	@Override
	@Transactional
	public AccountNewResponseDTO updateAccount(Integer id, AccountRequestDTO accountRequest) {
		System.out.println("Account update: Service");
		System.out.println("Update account Id: " + id);
		System.out.println(accountRequest);

		// Get account data from account microservice
		AccountEntity accountEntity = accountRepository.findByIdAndDeleted(id, false, true)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		// Update account data
		accountEntity.setName(accountRequest.getAccountName());
		accountEntity.setUpdatedBy(getUserId());
		accountEntity.setFormId(accountRequest.getFormId());
		accountRepository.save(accountEntity);

		// Update form submission data
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = accountNewRequestDTOToFormSubmissionRequestDTO(
				accountEntity, accountRequest);
		HttpResponse formSubmissionResponse = formSubmissionAPIClient
				.updateFormSubmission(accountEntity.getFormSubmissionId(), formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		// Update document data
		if (accountRequest.getUploadAgreement() != null) {

			// Delete old file and save new file
			HttpResponse documentResponse = documentAPIClient.deleteDocumentsByEntityTypeAndEntityId("account_account",
					accountEntity.getId());
			CommercialRequest.DocumentRequestDTO documentRequestDTO = new CommercialRequest.DocumentRequestDTO();
			// Save document and tag to account entity
			documentRequestDTO.setEntityId(accountEntity.getId());
			documentRequestDTO.setEntityType(ACCOUNT_TYPE);
			documentRequestDTO.setFile(accountRequest.getUploadAgreement());
			HttpResponse documentResponseNew = documentAPIClient.createDocument(documentRequestDTO);
			DocumentResponseDTO documentData = MappingUtil.mapClientBodyToClass(documentResponseNew.getData(),
					DocumentResponseDTO.class);
		} else if (accountRequest.getIsDeleteFile()) {
			HttpResponse documentResponse = documentAPIClient.deleteDocumentsByEntityTypeAndEntityId("account_account",
					accountEntity.getId());
		}

		// Added - Update accountSubmissionData
		accountEntity.setAccountSubmissionData(formSubmissionsRequestDTO.getSubmissionData());

		return accountEntityToAccountResponseDTO(accountEntity);
	}

	@Override
	public List<AccountNameResponseDTO> getAllAccountsName() {
		List<AccountEntity> accountEntities = accountRepository.findAllByUserAndDraftAndDeleted(getUserId(), false,
				false, true);
		return accountEntities.stream().map(this::accountNewEntityToAccountNameResponseDTO).toList();
	}

	@Override
	public List<AccountNameResponseDTO> getAllAccountsNameAll() {
		List<AccountEntity> accountEntities = accountRepository.findAllByIsDraftAndIsDeletedAndIsActive(false, false,
				true);
		return accountEntities.stream().map(this::accountNewEntityToAccountNameResponseDTO).toList();
	}

	@Override
	public List<Map<String, String>> getAllAccountsFields() {
		List<AccountEntity> accountEntities = accountRepository.findAllByUserAndDeleted(getUserId(), false, true);
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
		for (AccountEntity accountEntity : accountEntities) {
			if (accountEntity.getAccountSubmissionData() != null) {
				Iterator<String> accountFieldNames = accountEntity.getAccountSubmissionData().fieldNames();
				while (accountFieldNames.hasNext()) {
					String fieldName = accountFieldNames.next();
					keyMap.put(StringUtil.convertCamelCaseToTitleCase2(fieldName),
							"accountSubmissionData." + fieldName);
				}
			}

			if (accountEntity.getCommercialSubmissionData() != null) {
				Iterator<String> commercialFieldNames = accountEntity.getCommercialSubmissionData().fieldNames();
				while (commercialFieldNames.hasNext()) {
					String fieldName = commercialFieldNames.next();
					keyMap.put(StringUtil.convertCamelCaseToTitleCase2(fieldName),
							"commercialSubmissionData." + fieldName);
				}
			}
		}

		List<Map<String, String>> fieldOptions = new ArrayList<>();
		// Loop Through map
		for (Map.Entry<String, String> entry : keyMap.entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
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

		List<AccountEntity> accountEntities = accountRepository.findAllByIsDraftAndIsDeletedAndIsActive(false, false,
				true);

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
		for (AccountEntity accountEntity : accountEntities) {
			if (accountEntity.getAccountSubmissionData() != null) {
				Iterator<String> accountFieldNames = accountEntity.getAccountSubmissionData().fieldNames();
				while (accountFieldNames.hasNext()) {
					String fieldName = accountFieldNames.next();
					fieldColumn.add(new FieldInformation(StringUtil.convertCamelCaseToTitleCase2(fieldName),
							"accountSubmissionData." + fieldName, true, "account_submission_data." + fieldName));
				}
			}

			if (accountEntity.getCommercialSubmissionData() != null) {
				Iterator<String> commercialFieldNames = accountEntity.getCommercialSubmissionData().fieldNames();
				while (commercialFieldNames.hasNext()) {
					String fieldName = commercialFieldNames.next();
					fieldColumn.add(new FieldInformation(StringUtil.convertCamelCaseToTitleCase2(fieldName),
							"commercialSubmissionData." + fieldName, true, "commercial_submission_data." + fieldName));
				}
			}
		}
		return fieldColumn;
	}

	@Override
	public AccountListingResponseDTO getAccountListingPage(Integer page, Integer size, String sortBy,
			String sortDirection, Boolean isGetAll, Boolean isDownload) {
		// Get sort direction
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null && !sortDirection.isEmpty()) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null || sortBy.isEmpty() || sortBy.equals("")) {
			sortBy = "updated_at";
			direction = Sort.Direction.DESC;
		}

		PageRequest pageRequest = null;
		if (isDownload) {
			pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(direction, sortBy));
		} else {
			pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
		}

		Page<AccountEntity> accountEntitiesPage = null;

		List<Long> userIds = new ArrayList<>();
		List<String> userNamesEmail = new ArrayList<>();
		if (!isGetAll) {
			userIds = userUtil.getUsersIdUnderManager();
			userNamesEmail = userUtil.getUserNameEmailUnderManager();
		}

		String accountOwnerValue = userUtil.getUserNameEmail();
		// Try with numeric first else try with string (jsonb)
		try {
			accountEntitiesPage = accountRepository.findAllByOrderByNumericWithUserIds(userIds, false, false, true,
					userNamesEmail, pageRequest);
		} catch (Exception e) {
			accountEntitiesPage = accountRepository.findAllByOrderByStringWithUserIds(userIds, false, false, true,
					userNamesEmail, pageRequest);
		}

		return pageAccountListingToAccountListingResponseDTO(accountEntitiesPage);
	}

	@Override
	public AccountListingResponseDTO getAccountListingPageWithSearch(Integer page, Integer size, String sortBy,
			String sortDirection, String searchTerm, List<String> searchFields, Boolean isGetAll, Boolean isDownload) {
		// Get sort direction
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null) {
			sortBy = "updated_at";
			direction = Sort.Direction.DESC;
		}

		PageRequest pageRequest = null;
		if (isDownload) {
			pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(direction, sortBy));
		} else {
			pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
		}

		Page<AccountEntity> accountEntitiesPage = null;

		List<String> userNamesEmail = new ArrayList<>();
		List<Long> userIds = new ArrayList<>();
		if (!isGetAll) {
			userIds = userUtil.getUsersIdUnderManager();
			userNamesEmail = userUtil.getUserNameEmailUnderManager();
		}

		// Try with numeric first else try with string (jsonb)
		try {
			accountEntitiesPage = accountRepository.findAllByOrderByAndSearchNumericWithUserIds(userIds, false, false,
					true, userNamesEmail, pageRequest, searchFields, searchTerm);
		} catch (Exception e) {
			accountEntitiesPage = accountRepository.findAllByOrderByAndSearchStringWithUserIds(userIds, false, false,
					true, userNamesEmail, pageRequest, searchFields, searchTerm);
		}

		return pageAccountListingToAccountListingResponseDTO(accountEntitiesPage);
	}

	@Override
	@Transactional
	public void deleteDraftAccount(Integer accountId) {
		// Get account which is in draft state.
		AccountEntity accountEntityFound = accountRepository.findByIdAndDraft(accountId, true, true)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		// Delete all contacts belong to this account
		HttpResponse contactResponse = contactAPIClient.deleteContactsByEntityTypeAndEntityId("account_contact",
				accountId);
		// Delete all Documents
		HttpResponse documentResponse = documentAPIClient.deleteDocumentsByEntityTypeAndEntityId("account_document",
				accountId);
		// Delete account client instructions
		HttpResponse instructionResponse = instructionAPIClient.deleteInstructionByEntityId("account_instruction",
				accountId);
		// Delete all client instruction documents
		HttpResponse instructionDocumentResponse = documentAPIClient
				.deleteDocumentsByEntityTypeAndEntityId("account_instruction_document", accountId);

		// Delete all the commercial form submission if it exist
		if (accountEntityFound.getCommercialFormSubmissionId() != null) {
			HttpResponse commercialFormSubmissionResponse = formSubmissionAPIClient
					.deleteFormSubmission(accountEntityFound.getCommercialFormSubmissionId());
		}

		// Delete all account form submission
		if (accountEntityFound.getFormSubmissionId() != null) {
			HttpResponse formSubmissionResponse = formSubmissionAPIClient
					.deleteFormSubmission(accountEntityFound.getFormSubmissionId());
		}

		// Delete account required document
		HttpResponse requiredDocumentResponse = documentAPIClient
				.deleteDocumentsByEntityTypeAndEntityId("account_account", accountId);

		// Check if there is parent company and delete it
		accountEntityFound.setParentCompany(null);
		accountRepository.delete(accountEntityFound);
		System.out.println("Draft account deleted");
	}

	@Override
	public void softDeleteAccount(Integer accountId) {
		AccountEntity accountEntityFound = accountRepository.findByIdAndDeleted(accountId, false, true)
				.orElseThrow(() -> new RuntimeException("Account not found"));

		// Soft delete the account
		accountEntityFound.setIsDeleted(true);

		// Save account
		accountRepository.save(accountEntityFound);
	}

	@Override
	public List<AccountEntity> getAllAccountsNameWithSearch(String query) {
		List<AccountEntity> accountEntities = accountRepository.getAllAccountsNameWithSearch(query, getUserId(), false,
				false);
		return accountEntities;
	}

	@Override
	public List<AccountEntity> getAllAccountsByUser(boolean draft, boolean deleted) {
		List<AccountEntity> accountEntities = accountRepository.findAllByUserAndDraftAndDeleted(getUserId(), draft,
				deleted, true);
		return accountEntities;
	}

	/**
	 * Get account Data (Only account microservice)
	 * 
	 * @param accountId
	 * @return
	 */
	@Override
	public AccountListingDataDTO getAccountByIdData(Integer accountId) {
		return accountEntityToAccountNewListingDataDTO(accountRepository.findByIdAndDeleted(accountId, false, true)
				.orElseThrow(() -> new RuntimeException("Account not found")));
	}

	/**
	 * Get all account fields including all related microservices
	 * 
	 * @param accountId
	 * @return
	 */
	@Override
	public HashMap<String, List<HashMap<String, String>>> getAllAccountsFieldsAll() {
		HashMap<String, List<HashMap<String, String>>> allFields = new HashMap<>();

		// Get account fields from account microservice
		List<HashMap<String, String>> accountFields = getAccountFields();
		allFields.put("accountInfo", accountFields);

		// Get contact fields from contact microservice
		HttpResponse accountFormFieldResponse = formSubmissionAPIClient.getFormFieldNameList("account_contact");
		List<HashMap<String, String>> accountContactFields = MappingUtil
				.mapClientBodyToClass(accountFormFieldResponse.getData(), List.class);
		allFields.put("accountContact", accountContactFields);

		// Get Document fields from instruction microservice
		HttpResponse documentFormFieldResponse = formSubmissionAPIClient.getFormFieldNameList("account_instruction");
		List<HashMap<String, String>> accountInstructionFields = MappingUtil
				.mapClientBodyToClass(documentFormFieldResponse.getData(), List.class);
		allFields.put("accountInstruction", accountInstructionFields);

		return allFields;
	}

	/**
	 * Get all account data including all related microservices
	 * 
	 * @param accountId
	 * @return
	 */
	@Override
	public HashMap<String, Object> getAccountByIdDataAll(Integer accountId) {
		HashMap<String, Object> accountData = new HashMap<>();
		// Get account fields from account microservice
		JsonNode accountInfo = getAccountInfoByIDJsonNode(accountId);
		accountData.put("accountInfo", accountInfo);

		// Get contact fields from contact microservice
		HttpResponse accountContactResponse = contactAPIClient.getContactsByEntityTypeAndEntityId("account_contact",
				accountId);
		List<Object> accountContactData = MappingUtil.mapClientBodyToClass(accountContactResponse.getData(),
				List.class);
		List<JsonNode> accountContactJsonNode = MappingUtil.convertObjectToListOfJsonNode(accountContactData,
				"submissionData");
		accountData.put("accountContact", accountContactJsonNode);

		// Get instruction fields from instruction microservice
		HttpResponse accountInstructionResponse = instructionAPIClient.getInstructionByEntityId("account_instruction",
				accountId);
		Object accountInstructionData = MappingUtil.mapClientBodyToClass(accountInstructionResponse.getData(),
				Object.class);
		accountData.put("accountInstruction",
				MappingUtil.convertObjectToJsonNode(accountInstructionData, "submissionData"));

		return accountData;
	}

	@Override
	public void softDeleteAccounts(AccountListingDeleteRequestDTO accountListingDeleteRequestDTO) {
		if (accountListingDeleteRequestDTO.getAccountIds().isEmpty()) {
			throw new RuntimeException("No account selected");
		}
		List<AccountEntity> accountEntities = accountRepository
				.findAllByIdsAndDraftAndDeleted(accountListingDeleteRequestDTO.getAccountIds(), false, false, true);

		if (accountEntities.isEmpty()) {
			throw new RuntimeException("No account found");
		}

		for (AccountEntity accountEntity : accountEntities) {
			accountEntity.setIsDeleted(true);
		}

		accountRepository.saveAll(accountEntities);
	}

	private JsonNode getAccountInfoByIDJsonNode(Integer accountId) {
		// Get basic information from form submission
		AccountEntity accountEntity = accountRepository.findByIdAndDeleted(accountId, false, true)
				.orElseThrow(() -> new RuntimeException("Account not found"));
		// Get the form submission data from candidate microservice
		JsonNode accountSubmissionData = accountEntity.getAccountSubmissionData();
		JsonNode commercialSubmissionData = accountEntity.getCommercialSubmissionData();
		// Get additional data in JSon node format too
		JsonNode accountExtraDataJsonNode = getAccountExtraData(accountEntity).getSelectedFieldsJsonNode();
		return MappingUtil
				.mergeJsonNodes(List.of(accountSubmissionData, commercialSubmissionData, accountExtraDataJsonNode));
	}

	private List<HashMap<String, String>> getAccountFields() {
		AccountExtraData accountExtraData = new AccountExtraData();

		// Get account dynamic fields from form service
		HttpResponse accountFormFieldResponse = formSubmissionAPIClient.getFormFieldNameList("account_account");
		List<HashMap<String, String>> accountFields = MappingUtil
				.mapClientBodyToClass(accountFormFieldResponse.getData(), List.class);

		// Merge lists using addAll()
		List<HashMap<String, String>> mergedList = new ArrayList<>(accountExtraData.getAllFieldsMap()); // Copy contents
																										// of list1
		mergedList.addAll(accountFields);

		return mergedList;
	}

	private AccountExtraData getAccountExtraData(AccountEntity accountEntity) {
		AccountExtraData accountExtraData = new AccountExtraData(accountEntity);
		// Get created by User data from user microservice
		HttpResponse createUserResponse = userAPIClient.getUserById(accountEntity.getCreatedBy());
		UserResponseDTO createUserData = MappingUtil.mapClientBodyToClass(createUserResponse.getData(),
				UserResponseDTO.class);
		accountExtraData.setCreatedByName(createUserData.getFirstName() + " " + createUserData.getLastName());
		HttpResponse updateUserResponse = userAPIClient.getUserById(accountEntity.getUpdatedBy());
		UserResponseDTO updateUserData = MappingUtil.mapClientBodyToClass(updateUserResponse.getData(),
				UserResponseDTO.class);
		accountExtraData.setUpdatedByName(updateUserData.getFirstName() + " " + updateUserData.getLastName());
		return accountExtraData;
	}

	/**
	 * Page to account listing new response
	 */
	private AccountListingResponseDTO pageAccountListingToAccountListingResponseDTO(
			Page<AccountEntity> accountNewEntitiesPage) {
		AccountListingResponseDTO accountListingResponseDTO = new AccountListingResponseDTO();
		accountListingResponseDTO.setTotalElements(accountNewEntitiesPage.getTotalElements());
		accountListingResponseDTO.setTotalPages(accountNewEntitiesPage.getTotalPages());
		accountListingResponseDTO.setPage(accountNewEntitiesPage.getNumber());
		accountListingResponseDTO.setPageSize(accountNewEntitiesPage.getSize());
		List<AccountListingDataDTO> accountListingDataDTOS = new ArrayList<>();
		accountListingDataDTOS = accountNewEntitiesPage.getContent().stream().map(accountNewEntity -> {
			AccountListingDataDTO accountListingDataDTO = new AccountListingDataDTO(accountNewEntity);
			// Get created by User data from user microservice
			HttpResponse createUserResponse = userAPIClient.getUserById(accountNewEntity.getCreatedBy());
			UserResponseDTO createUserData = MappingUtil.mapClientBodyToClass(createUserResponse.getData(),
					UserResponseDTO.class);
			accountListingDataDTO.setCreatedByName(createUserData.getFirstName() + " " + createUserData.getLastName());
			HttpResponse updateUserResponse = userAPIClient.getUserById(accountNewEntity.getUpdatedBy());
			UserResponseDTO updateUserData = MappingUtil.mapClientBodyToClass(updateUserResponse.getData(),
					UserResponseDTO.class);
			accountListingDataDTO.setUpdatedByName(updateUserData.getFirstName() + " " + updateUserData.getLastName());
			return accountListingDataDTO;
		}).toList();

		accountListingResponseDTO.setAccounts(accountListingDataDTOS);
		return accountListingResponseDTO;
	}

	private AccountListingDataDTO accountEntityToAccountNewListingDataDTO(AccountEntity accountEntity) {
		AccountListingDataDTO accountListingDataDTO = new AccountListingDataDTO(accountEntity);
		// Get created by User data from user microservice
		HttpResponse createUserResponse = userAPIClient.getUserById(accountEntity.getCreatedBy());
		UserResponseDTO createUserData = MappingUtil.mapClientBodyToClass(createUserResponse.getData(),
				UserResponseDTO.class);
		accountListingDataDTO.setCreatedByName(createUserData.getFirstName() + " " + createUserData.getLastName());
		HttpResponse updateUserResponse = userAPIClient.getUserById(accountEntity.getUpdatedBy());
		UserResponseDTO updateUserData = MappingUtil.mapClientBodyToClass(updateUserResponse.getData(),
				UserResponseDTO.class);
		accountListingDataDTO.setUpdatedByName(updateUserData.getFirstName() + " " + updateUserData.getLastName());
		return accountListingDataDTO;
	}

	/**
	 * Internal method to convert AccountEntity to AccountNameReponseDTO
	 *
	 * @param accountEntity
	 * @return
	 */
	private AccountNameResponseDTO accountNewEntityToAccountNameResponseDTO(AccountEntity accountEntity) {
		AccountNameResponseDTO accountNameReponseDTO = new AccountNameResponseDTO();
		accountNameReponseDTO.setId(accountEntity.getId());
		accountNameReponseDTO.setName(accountEntity.getName());
		return accountNameReponseDTO;
	}

	private FormSubmissionsRequestDTO accountNewRequestDTOToFormSubmissionRequestDTO(AccountEntity accountEntity,
			AccountRequestDTO accountRequestDTO) {
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(getUserId());
		formSubmissionsRequestDTO.setFormId(accountRequestDTO.getFormId());
		formSubmissionsRequestDTO
				.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(accountRequestDTO.getFormData()));
		formSubmissionsRequestDTO.setEntityId(accountEntity.getId());
		formSubmissionsRequestDTO.setEntityType(ACCOUNT_TYPE);
		return formSubmissionsRequestDTO;
	}

	private AccountNewResponseDTO accountEntityToAccountResponseDTO(AccountEntity accountEntity) {
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
			UserResponseDTO updatedByUserData = MappingUtil.mapClientBodyToClass(updatedByUserResponse.getData(),
					UserResponseDTO.class);
			accountResponseDTO.setUpdatedBy(updatedByUserData.getFirstName() + " " + updatedByUserData.getLastName());
		}

		// Get form submission data
		HttpResponse formSubmissionResponse = formSubmissionAPIClient
				.getFormSubmission(accountEntity.getFormSubmissionId());
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
		accountResponseDTO
				.setSubmissionData(MappingUtil.convertJsonNodeToJSONString(formSubmissionData.getSubmissionData()));

		// Added - Get accountSubmissionData and add to response
		accountResponseDTO.setAccountSubmissionData(formSubmissionData.getSubmissionData());

		return accountResponseDTO;
	}

	private AccountEntity accountRequestDTOToAccountEntity(AccountRequestDTO accountRequest) {
		AccountEntity accountEntity = new AccountEntity();
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
