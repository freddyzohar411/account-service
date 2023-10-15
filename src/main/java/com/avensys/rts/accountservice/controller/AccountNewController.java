package com.avensys.rts.accountservice.controller;

import com.avensys.rts.accountservice.constant.MessageConstants;
import com.avensys.rts.accountservice.payloadnewrequest.AccountNewRequestDTO;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNewResponseDTO;
import com.avensys.rts.accountservice.payloadrequest.AccountRequestDTO;
import com.avensys.rts.accountservice.payloadresponse.AccountResponseDTO;
import com.avensys.rts.accountservice.service.AccountNewServiceImpl;
import com.avensys.rts.accountservice.service.AccountServiceImpl;
import com.avensys.rts.accountservice.util.ResponseUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
//        String formData = accountRequest.getFormData();
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            JsonNode jsonNode = objectMapper.readTree(formData);
//            System.out.println("JsonNode: " + jsonNode);
//            // Now, jsonNode contains your JSON data
//            // ...
//        } catch (IOException e) {
//            System.out.println("Exception: " + e.getMessage());
//            // Handle exception
//        }
//        System.out.println("Form Data: " + formData);
        AccountResponseDTO account = accountService.createAccount(accountRequest);
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.CREATED, messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
//        return ResponseUtil.generateSuccessResponse(null, HttpStatus.CREATED, messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));

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

}
