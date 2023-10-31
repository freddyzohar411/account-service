package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.entity.AccountNewEntity;
import com.avensys.rts.accountservice.model.FieldInformation;
import com.avensys.rts.accountservice.payloadnewrequest.AccountNewRequestDTO;
import com.avensys.rts.accountservice.payloadnewrequest.CommercialNewRequest;
import com.avensys.rts.accountservice.payloadnewresponse.AccountListingNewResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNewResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.CommercialNewResponseDTO;
import com.avensys.rts.accountservice.payloadrequest.AccountRequestDTO;
import com.avensys.rts.accountservice.payloadresponse.AccountListingResponseDTO;
import com.avensys.rts.accountservice.payloadresponse.AccountNameReponseDTO;
import com.avensys.rts.accountservice.payloadresponse.AccountResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AccountNewService {
    AccountNewResponseDTO createAccount(AccountNewRequestDTO accountRequest);

    AccountNewResponseDTO getAccount(Integer id);

    AccountNewResponseDTO getAccountIfDraft();

    AccountNewResponseDTO updateAccount(Integer id, AccountNewRequestDTO accountRequest);

    /**
     * This method is used to get all accounts name
     * @return
     */
    List<AccountNameReponseDTO> getAllAccountsName();

    List<Map<String, String>> getAllAccountsFields();

    Set<FieldInformation> getAllAccountsFieldsNew();

    AccountListingNewResponseDTO getAccountListingPage(Integer page, Integer size, String sortBy, String sortDirection);

    AccountListingNewResponseDTO getAccountListingPageWithSearch(Integer page, Integer size, String sortBy, String sortDirection, String searchTerm, List<String>searchFields);

    void deleteDraftAccount(Integer accountId);

    void softDeleteAccount(Integer accountId);

    List<AccountNewEntity> getAllAccountsNameWithSearch(String query);

    List<AccountNewEntity> getAllAccountsByUser(boolean draft, boolean deleted);



}
