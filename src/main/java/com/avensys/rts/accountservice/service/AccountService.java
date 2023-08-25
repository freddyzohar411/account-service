package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.payload.AccountRequestDTO;
import com.avensys.rts.accountservice.payload.AccountResponseDTO;

import java.util.List;
import java.util.Optional;

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
     * This method is used to get account by id
     * @param id
     * @return
     */
    AccountResponseDTO getAccountById(Long id);

    /**
     * This method is used to update an account
     * @param accountRequest
     * @return
     */
    AccountResponseDTO updateAccount(AccountRequestDTO accountRequest);

    /**
     * This method is used to delete an account
     * @param id
     */
    void deleteAccount(Long id);


}
