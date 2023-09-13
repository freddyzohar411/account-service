package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.payloadrequest.AccountRequestDTO;
import com.avensys.rts.accountservice.payloadrequest.CommercialRequestDTO;
import com.avensys.rts.accountservice.payloadresponse.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Koh He Xiang
 * This interface is used to declare methods for AccountService
 */
public interface AccountService {

    /**
     * This method is used to create account
     * @param accountRequest
     * @return
     */
    AccountResponseDTO createAccount(AccountRequestDTO accountRequest);

    /**
     * This method is used to get all accounts
     * @return
     */
    List<AccountResponseDTO> getAllAccounts();

    /**
     * This method is used to get all accounts name
     * @return
     */
    List<AccountNameReponseDTO> getAllAccountsName();

    /**
     * This method is used to get all accounts
     * @return
     */
    List<AccountEntity> getAllAccountsEntity();


    /**
     * This method is used to get account by id
     * @param id
     * @return
     */
    AccountResponseDTO getAccountById(int id);

    /**
     * This method is used to update an account
     * @param accountRequest
     * @return
     */
    AccountResponseDTO updateAccount(int accountId, AccountRequestDTO accountRequest);

    /**
     * This method is used to delete an account
     * @param id
     */
    void deleteAccountById(int id);

    /**
     * This method is used to set the account commercial
     * @param accountId
     * @param commercialRequest
     * @return AccountResponseDTO
     */
    CommercialResponseDTO setAccountCommercial (int accountId, CommercialRequestDTO commercialRequest);

    /**
     * This method is used to get the account commercial
     * @param accountId
     * @return
     */
    CommercialResponseDTO getAccountCommercialById (int accountId);

    /**
     * This method is used to get the account if it is a draft
     * @return
     */
    AccountResponseDTO getAccountIfDraft();

    /**
     * This method is used to get all accounts by pagination and sort
     * @param page
     * @param size
     * @param sort
     * @param sortDir
     * @return
     */
    public AccountListingResponseDTO getAllAccountsByPaginationAndSort(Integer page, Integer size, String sort, String sortDir);

    /**
     * This method is used to get all accounts by pagination and sort and search
     * @param page
     * @param size
     * @param sort
     * @param sortDir
     * @param search
     * @return
     */
    public AccountListingResponseDTO getAllAccountsByPaginationAndSortAndSearch(Integer page, Integer size, String sort, String sortDir, String search);
}
