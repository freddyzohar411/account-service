package com.avensys.rts.accountservice.controller;

import com.avensys.rts.accountservice.constant.MessageConstants;
import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.payloadrequest.AccountRequestDTO;
import com.avensys.rts.accountservice.payloadresponse.AccountResponseDTO;
import com.avensys.rts.accountservice.service.AccountServiceImpl;
import com.avensys.rts.accountservice.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/***
 * @author Koh He Xiang
 * This class is used to handle the API calls for the account service.
 */

@RestController
public class AccountController {

    private final AccountServiceImpl accountService;
    private final MessageSource messageSource;

    public AccountController(AccountServiceImpl accountService, MessageSource messageSource) {
        this.accountService = accountService;
        this.messageSource = messageSource;
    }

    /**
     * Create an account draft
     * @param accountRequest
     * @return
     */
    @PostMapping("/accounts")
    public ResponseEntity<Object> addAccount(@Valid @ModelAttribute AccountRequestDTO accountRequest) {
        System.out.println("In saving account now...");
        AccountEntity account = accountService.createAccount(accountRequest);
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.CREATED, messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Find an account by Id
     * @param accountId
     * @return
     */
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Object> getAccountById(@PathVariable int accountId){
        AccountResponseDTO account = accountService.getAccountById(accountId);
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get all accounts
     * @return
     */
    @GetMapping("/accounts")
    public ResponseEntity<Object> getAllAccounts() {
        List<AccountResponseDTO> accounts =  accountService.getAllAccounts();
        Map<String, Object> accountsMap = Map.of("accounts", accounts);
        return ResponseUtil.generateSuccessResponse(accountsMap, HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Update an account
     * @param accountId
     * @param accountRequest
     */
    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<Object> updateAccount(@PathVariable int accountId, @Valid @ModelAttribute AccountRequestDTO accountRequest) {
        AccountResponseDTO account = accountService.updateAccount(accountId, accountRequest);
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Delete an account
     * @param accountId
     * @return
     */
    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<Object> deleteAccountById(@PathVariable int accountId) {
        accountService.deleteAccountById(accountId);
        return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

}
