package com.avensys.rts.accountservice.controller;

import com.avensys.rts.accountservice.annotation.RequiresAllPermissions;
import com.avensys.rts.accountservice.annotation.RequiresAnyPermission;
import com.avensys.rts.accountservice.annotation.RequiresAnyRole;
import com.avensys.rts.accountservice.constant.MessageConstants;
import com.avensys.rts.accountservice.enums.Permission;
import com.avensys.rts.accountservice.enums.Role;
import com.avensys.rts.accountservice.payloadnewrequest.AccountListingRequestDTO;
import com.avensys.rts.accountservice.payloadnewrequest.AccountNewRequestDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNewResponseDTO;
import com.avensys.rts.accountservice.service.AccountNewServiceImpl;
import com.avensys.rts.accountservice.util.ResponseUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AccountNewController {

    private final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountNewServiceImpl accountService;
    private final MessageSource messageSource;

    public AccountNewController(AccountNewServiceImpl accountService, MessageSource messageSource) {
        this.accountService = accountService;
        this.messageSource = messageSource;
    }

    /**
     * Create an account draft
     * @param accountRequest
     * @return
     */
    @PostMapping("/accounts")
    public ResponseEntity<Object> addAccount(@Valid @ModelAttribute AccountNewRequestDTO accountRequest) {
        log.info("Account create: Controller");
        AccountNewResponseDTO account = accountService.createAccount(accountRequest);
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.CREATED, messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<Object> getAccount(@PathVariable int accountId) {
        log.info("Account get: Controller");
        AccountNewResponseDTO account = accountService.getAccount(accountId);
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    @GetMapping("/accounts/draft")
    public ResponseEntity<Object> getAccountIfDraft() {
        log.info("Account get: Controller");
        AccountNewResponseDTO account = accountService.getAccountIfDraft();
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<Object> updateAccount(@PathVariable int accountId,@ModelAttribute AccountNewRequestDTO accountRequest) {
        log.info("Account update: Controller");
        AccountNewResponseDTO account = accountService.updateAccount(accountId, accountRequest);
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    @GetMapping("/accounts/names")
    public ResponseEntity<Object> getAllAccountsName() {
        log.info("Account get all name: Controller");
        return ResponseUtil.generateSuccessResponse(accountService.getAllAccountsName(), HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    @GetMapping("/accounts/fields")
    public ResponseEntity<Object> getAllAccountsFields() {
        log.info("Account get all fields: Controller");
        return ResponseUtil.generateSuccessResponse(accountService.getAllAccountsFieldsNew(), HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

//    @RequiresAllPermissions({Permission.READ})
    @PostMapping("/accounts/listing")
    public ResponseEntity<Object> getAccountListing(@RequestBody AccountListingRequestDTO accountListingRequestDTO)  {
        log.info("Account get all fields: Controller");
        Integer page = accountListingRequestDTO.getPage();
        Integer pageSize = accountListingRequestDTO.getPageSize();
        String sortBy = accountListingRequestDTO.getSortBy();
        String sortDirection = accountListingRequestDTO.getSortDirection();
        String searchTerm = accountListingRequestDTO.getSearchTerm();
        List<String> searchFields = accountListingRequestDTO.getSearchFields();
        System.out.println(("Test 1"));
        System.out.println("Page: " + page);
        System.out.println("PageSize: " + pageSize);
        System.out.println("SortBy: " + sortBy);
        System.out.println("SortDirection: " + sortDirection);
        System.out.println("SearchTerm: " + searchTerm);
        System.out.println("SearchFields: " + searchFields);
        if (searchTerm == null || searchTerm.isEmpty()) {
            System.out.println(("Test 2"));
            return ResponseUtil.generateSuccessResponse(accountService.getAccountListingPage(page, pageSize, sortBy, sortDirection), HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
        }
        return ResponseUtil.generateSuccessResponse(accountService.getAccountListingPageWithSearch(
                page, pageSize, sortBy, sortDirection, searchTerm, searchFields), HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Hard delete draft account
     * @param accountId
     * @return
     */
    @DeleteMapping("accounts/draft/{accountId}")
    public ResponseEntity<Object> deleteDraftAccount(@PathVariable int accountId) {
        log.info("Account delete: Controller");
        accountService.deleteDraftAccount(accountId);
        return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Soft delete existing account
     */
    @DeleteMapping("accounts/{accountId}")
    public ResponseEntity<Object> softDeleteAccount(@PathVariable int accountId) {
        log.info("Account soft delete: Controller");
        accountService.softDeleteAccount(accountId);
        return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

}
