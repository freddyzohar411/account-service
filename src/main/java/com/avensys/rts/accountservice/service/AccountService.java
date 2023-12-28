package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.model.FieldInformation;
import com.avensys.rts.accountservice.payloadnewrequest.AccountRequestDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountListingResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNameResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountListingDataDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNewResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AccountService {
    AccountNewResponseDTO createAccount(AccountRequestDTO accountRequest);

    AccountNewResponseDTO getAccount(Integer id);

    AccountNewResponseDTO getAccountIfDraft();

    AccountNewResponseDTO updateAccount(Integer id, AccountRequestDTO accountRequest);

    /**
     * This method is used to get all accounts name
     * @return
     */
    List<AccountNameResponseDTO> getAllAccountsName();

    List<AccountNameResponseDTO> getAllAccountsNameAll();

    List<Map<String, String>> getAllAccountsFields();

    Set<FieldInformation> getAllAccountsFieldsNew();

    AccountListingResponseDTO getAccountListingPage(Integer page, Integer size, String sortBy, String sortDirection);

    AccountListingResponseDTO getAccountListingPageWithSearch(Integer page, Integer size, String sortBy, String sortDirection, String searchTerm, List<String>searchFields);

    void deleteDraftAccount(Integer accountId);

    void softDeleteAccount(Integer accountId);

    List<AccountEntity> getAllAccountsNameWithSearch(String query);

    List<AccountEntity> getAllAccountsByUser(boolean draft, boolean deleted);

    AccountListingDataDTO getAccountByIdData(Integer accountId);

}
