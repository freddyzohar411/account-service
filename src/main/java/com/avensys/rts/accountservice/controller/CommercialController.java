package com.avensys.rts.accountservice.controller;

import com.avensys.rts.accountservice.constant.MessageConstants;
import com.avensys.rts.accountservice.payloadnewrequest.AccountNewRequestDTO;
import com.avensys.rts.accountservice.payloadnewrequest.CommercialNewRequest;
import com.avensys.rts.accountservice.payloadnewresponse.CommercialNewResponseDTO;
import com.avensys.rts.accountservice.payloadresponse.AccountResponseDTO;
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

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommercialController {

    private final Logger log = LoggerFactory.getLogger(CommercialController.class);

    private final AccountNewServiceImpl accountService;
    private final MessageSource messageSource;

    public CommercialController(AccountNewServiceImpl accountService, MessageSource messageSource) {
        this.accountService = accountService;
        this.messageSource = messageSource;
    }

    /**
     * Create a commercial
     * @param accountId
     * @param commercialRequest
     * @return
     */
    @PostMapping("/commercials/{accountId}")
    public ResponseEntity<Object> addCommercial(@PathVariable int accountId, @RequestBody CommercialNewRequest commercialRequest) {
        log.info("Account create: Controller");
        CommercialNewResponseDTO commercialNewResponse = accountService.createCommercial(accountId, commercialRequest);
        return ResponseUtil.generateSuccessResponse(commercialNewResponse, HttpStatus.CREATED, messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Update a commercial
     * @param accountId
     * @param commercialRequest
     * @return
     */
    @PutMapping("/commercials/{accountId}")
    public ResponseEntity<Object> updateCommercial(@PathVariable int accountId, @RequestBody CommercialNewRequest commercialRequest) {
        log.info("Account create: Controller");
        CommercialNewResponseDTO commercialNewResponse = accountService.updateCommercial(accountId, commercialRequest);
        return ResponseUtil.generateSuccessResponse(commercialNewResponse, HttpStatus.CREATED, messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get a commercial
     * @param accountId
     * @return
     */
    @GetMapping("/commercials/{accountId}")
    public ResponseEntity<Object> getCommercial(@PathVariable int accountId) {
        log.info("Account get: Controller");
        CommercialNewResponseDTO commercialNewResponse = accountService.getCommercial(accountId);
        return ResponseUtil.generateSuccessResponse(commercialNewResponse, HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }
}
