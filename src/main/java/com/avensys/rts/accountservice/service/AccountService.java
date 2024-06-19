package com.avensys.rts.accountservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.entity.CustomFieldsEntity;
import com.avensys.rts.accountservice.model.FieldInformation;
import com.avensys.rts.accountservice.payloadnewrequest.AccountListingDeleteRequestDTO;
import com.avensys.rts.accountservice.payloadnewrequest.AccountRequestDTO;
import com.avensys.rts.accountservice.payloadnewrequest.CustomFieldsRequestDTO;
import com.avensys.rts.accountservice.payloadnewrequest.FilterDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountListingDataDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountListingResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNameResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNewResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.CustomFieldsResponseDTO;

public interface AccountService {
	AccountNewResponseDTO createAccount(AccountRequestDTO accountRequest);

	// new
	CustomFieldsResponseDTO saveCustomFields(CustomFieldsRequestDTO customFieldsRequestDTO);

	List<CustomFieldsEntity> getAllCreatedCustomViews();

	CustomFieldsResponseDTO updateCustomView(Long id);

	public void softDelete(Long id);

	// CustomFieldsResponseDTO getAccountCusotmView(Long id);

	AccountNewResponseDTO getAccount(Integer id);

	AccountNewResponseDTO getAccountIfDraft();

	AccountNewResponseDTO updateAccount(Integer id, AccountRequestDTO accountRequest);

	/**
	 * This method is used to get all accounts name
	 * 
	 * @return
	 */
	List<AccountNameResponseDTO> getAllAccountsName();

	List<AccountNameResponseDTO> getAllAccountsNameAll();

	List<Map<String, String>> getAllAccountsFields();

	Set<FieldInformation> getAllAccountsFieldsNew();

	AccountListingResponseDTO getAccountListingPage(Integer page, Integer size, String sortBy, String sortDirection,
			Boolean isGetAll, Boolean isDownload, List<FilterDTO> filters);

	AccountListingResponseDTO getAccountListingPageWithSearch(Integer page, Integer size, String sortBy,
			String sortDirection, String searchTerm, List<String> searchFields, Boolean isGetAll, Boolean isDownload, List<FilterDTO> filters);

	void deleteDraftAccount(Integer accountId);

	void softDeleteAccount(Integer accountId);

	List<AccountEntity> getAllAccountsNameWithSearch(String query);

	List<AccountEntity> getAllAccountsByUser(boolean draft, boolean deleted);

	AccountListingDataDTO getAccountByIdData(Integer accountId);

	HashMap<String, List<HashMap<String, String>>> getAllAccountsFieldsAll();

	HashMap<String, Object> getAccountByIdDataAll(Integer candidateId);

	void softDeleteAccounts(AccountListingDeleteRequestDTO accountListingDeleteRequestDTO);

	CustomFieldsResponseDTO getCustomFieldsById(Long id);

	CustomFieldsResponseDTO editCustomFieldsById(Long id, CustomFieldsRequestDTO customFieldsRequestDTO);
}
