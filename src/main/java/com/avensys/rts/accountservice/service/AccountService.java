package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.payloadrequest.AccountRequestDTO;
import com.avensys.rts.accountservice.payloadresponse.AccountResponseDTO;

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
    AccountEntity createAccount(AccountRequestDTO accountRequest);

    /**
     * This method is used to get all accounts
     * @return
     */
    List<AccountResponseDTO> getAllAccounts();

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


}
