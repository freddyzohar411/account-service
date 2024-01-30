package com.avensys.rts.accountservice.controller;

import com.avensys.rts.accountservice.annotation.RequiresAllPermissions;
import com.avensys.rts.accountservice.constant.MessageConstants;
import com.avensys.rts.accountservice.enums.Permission;
import com.avensys.rts.accountservice.payloadnewrequest.AccountListingRequestDTO;
import com.avensys.rts.accountservice.payloadnewrequest.AccountRequestDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNewResponseDTO;
import com.avensys.rts.accountservice.service.AccountServiceImpl;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Koh He Xiang
 * This is the new controller class for the accounts that works with
 * dynamic forms
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/accounts")
public class AccountController {

    private final Logger log = LoggerFactory.getLogger(AccountController.class);
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
    @RequiresAllPermissions({Permission.ACCOUNT_WRITE})
    @PostMapping("/add")
    public ResponseEntity<Object> addAccount(@Valid @ModelAttribute AccountRequestDTO accountRequest) {
        log.info("Account create: Controller");
        AccountNewResponseDTO account = accountService.createAccount(accountRequest);
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.CREATED, messageSource.getMessage(MessageConstants.ACCOUNT_CREATED, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get an account by id
     * @param accountId
     * @return
     */
    @RequiresAllPermissions({Permission.ACCOUNT_READ})
    @GetMapping("/{accountId}")
    public ResponseEntity<Object> getAccount(@PathVariable int accountId) {
        log.info("Account get: Controller");
        AccountNewResponseDTO account = accountService.getAccount(accountId);
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.OK, messageSource.getMessage(MessageConstants.ACCOUNT_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get an account draft if exists
     * @return
     */
    @RequiresAllPermissions({Permission.ACCOUNT_WRITE})
    @GetMapping("/draft")
    public ResponseEntity<Object> getAccountIfDraft() {
        log.info("Account get: Controller");
        AccountNewResponseDTO account = accountService.getAccountIfDraft();
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.OK, messageSource.getMessage(MessageConstants.ACCOUNT_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Update an account draft or existing account
     * @param accountId
     * @param accountRequest
     * @return
     */
    @RequiresAllPermissions({Permission.ACCOUNT_EDIT})
    @PutMapping("/{accountId}")
    public ResponseEntity<Object> updateAccount(@PathVariable int accountId, @ModelAttribute AccountRequestDTO accountRequest) {
        log.info("Account update: Controller");
        AccountNewResponseDTO account = accountService.updateAccount(accountId, accountRequest);
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.OK, messageSource.getMessage(MessageConstants.ACCOUNT_UPDATED, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get all accounts with id and names (For user)
     * @return
     */
    @GetMapping("/names")
    public ResponseEntity<Object> getAllAccountsName() {
        log.info("Account get all name: Controller");
        return ResponseUtil.generateSuccessResponse(accountService.getAllAccountsName(), HttpStatus.OK, messageSource.getMessage(MessageConstants.ACCOUNT_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get all accounts with id and names (All)
     * @return
     */
    @GetMapping("/names-all")
    public ResponseEntity<Object> getAllAccountsNameAll() {
        log.info("Account get all name: Controller");
        return ResponseUtil.generateSuccessResponse(accountService.getAllAccountsNameAll(), HttpStatus.OK, messageSource.getMessage(MessageConstants.ACCOUNT_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get all accounts field for all forms related to accounts
     * @return
     */
    @GetMapping("/fields")
    public ResponseEntity<Object> getAllAccountsFields() {
        log.info("Account get all fields: Controller");
        return ResponseUtil.generateSuccessResponse(accountService.getAllAccountsFieldsNew(), HttpStatus.OK, messageSource.getMessage(MessageConstants.ACCOUNT_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get all accounts field for all forms related to accounts
     * with search and pagination
     *
     * @param accountListingRequestDTO
     * @return
     */
    @RequiresAllPermissions({Permission.ACCOUNT_READ})
    @PostMapping("/listing")
    public ResponseEntity<Object> getAccountListing(@RequestBody AccountListingRequestDTO accountListingRequestDTO) {
        log.info("Account get all fields: Controller");
        Integer page = accountListingRequestDTO.getPage();
        Integer pageSize = accountListingRequestDTO.getPageSize();
        String sortBy = accountListingRequestDTO.getSortBy();
        String sortDirection = accountListingRequestDTO.getSortDirection();
        String searchTerm = accountListingRequestDTO.getSearchTerm();
        List<String> searchFields = accountListingRequestDTO.getSearchFields();
        if (searchTerm == null || searchTerm.isEmpty()) {
            return ResponseUtil.generateSuccessResponse(accountService.getAccountListingPage(page, pageSize, sortBy, sortDirection, false), HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
        }
        return ResponseUtil.generateSuccessResponse(accountService.getAccountListingPageWithSearch(
                page, pageSize, sortBy, sortDirection, searchTerm, searchFields, false), HttpStatus.OK, messageSource.getMessage(MessageConstants.ACCOUNT_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    @RequiresAllPermissions({Permission.ACCOUNT_READ})
    @PostMapping("/listing/admin")
    public ResponseEntity<Object> getAccountListingAdmin(@RequestBody AccountListingRequestDTO accountListingRequestDTO) {
        log.info("Account get all fields: Controller");
        Integer page = accountListingRequestDTO.getPage();
        Integer pageSize = accountListingRequestDTO.getPageSize();
        String sortBy = accountListingRequestDTO.getSortBy();
        String sortDirection = accountListingRequestDTO.getSortDirection();
        String searchTerm = accountListingRequestDTO.getSearchTerm();
        List<String> searchFields = accountListingRequestDTO.getSearchFields();
        if (searchTerm == null || searchTerm.isEmpty()) {
            return ResponseUtil.generateSuccessResponse(accountService.getAccountListingPage(page, pageSize, sortBy, sortDirection, true), HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
        }
        return ResponseUtil.generateSuccessResponse(accountService.getAccountListingPageWithSearch(
                page, pageSize, sortBy, sortDirection, searchTerm, searchFields, true), HttpStatus.OK, messageSource.getMessage(MessageConstants.ACCOUNT_SUCCESS, null, LocaleContextHolder.getLocale()));
    }


    /**
     * Hard delete draft account
     * @param accountId
     * @return
     */
    @RequiresAllPermissions({Permission.ACCOUNT_WRITE})
    @DeleteMapping("/draft/{accountId}")
    public ResponseEntity<Object> deleteDraftAccount(@PathVariable int accountId) {
        log.info("Account delete: Controller");
        accountService.deleteDraftAccount(accountId);
        return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK, messageSource.getMessage(MessageConstants.ACCOUNT_DELETED, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Soft delete existing account
     */
    @RequiresAllPermissions({Permission.ACCOUNT_DELETE})
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Object> softDeleteAccount(@PathVariable int accountId) {
        log.info("Account soft delete: Controller");
        accountService.softDeleteAccount(accountId);
        return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK, messageSource.getMessage(MessageConstants.ACCOUNT_DELETED, null, LocaleContextHolder.getLocale()));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAccount(@RequestParam(
            value = "query",
            required = false
    ) String query) {
        log.info("Account search: Controller");
        if (query != null) {
            System.out.println("Query: " + query);
            String regex = "(\\w+)([><]=?|!=|=)(\\w+)";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(query);

            while (matcher.find()) {
                String fieldName = matcher.group(1);
                String operator = matcher.group(2);
                String value = matcher.group(3);

                // Now you have fieldName, operator, and value for each key-value pair
                System.out.println("Field Name: " + fieldName);
                System.out.println("Operator: " + operator);
                System.out.println("Value: " + value);
            }
        }

        if (query == null || query.isEmpty()) {
            return ResponseUtil.generateSuccessResponse(accountService.getAllAccountsByUser(false, false), HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
        }
        return ResponseUtil.generateSuccessResponse(accountService.getAllAccountsNameWithSearch(query), HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get account data (Only account info)
     * @param accountId
     * @return
     */
    @GetMapping("/{accountId}/data")
    public ResponseEntity<Object> getAccountByIdData(@PathVariable Integer accountId) {
        log.info("Account get by id data: Controller");
        return ResponseUtil.generateSuccessResponse(accountService.getAccountByIdData(accountId), HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get all account data inlcuding all related microservices
     * @param accountId
     * @return
     */
    @GetMapping("/{accountId}/data/all")
    public ResponseEntity<Object> getAccountByIdDataAll(@PathVariable Integer accountId) {
        log.info("Account get by id data: Controller");
        return ResponseUtil.generateSuccessResponse(accountService.getAccountByIdDataAll(accountId), HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get all account fields for including all related microservices
     * @return
     */
    @GetMapping("/fields/all")
    public ResponseEntity<Object> getAllAccountsFieldsAll() {
        log.info("Account get by id data: Controller");
        return ResponseUtil.generateSuccessResponse(accountService.getAllAccountsFieldsAll(), HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }

}
