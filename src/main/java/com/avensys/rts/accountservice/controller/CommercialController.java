package com.avensys.rts.accountservice.controller;

import com.avensys.rts.accountservice.constant.MessageConstants;
import com.avensys.rts.accountservice.payloadnewrequest.CommercialRequest;
import com.avensys.rts.accountservice.payloadnewresponse.CommercialResponseDTO;
import com.avensys.rts.accountservice.service.CommercialServiceImpl;
import com.avensys.rts.accountservice.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Author: Koh He Xiang
 * This is the controller class for the commercial service
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/commercials")
public class CommercialController {

    private final Logger log = LoggerFactory.getLogger(CommercialController.class);
    private final CommercialServiceImpl commercialService;
    private final MessageSource messageSource;

    public CommercialController( CommercialServiceImpl commercialService, MessageSource messageSource) {
        this.commercialService = commercialService;
        this.messageSource = messageSource;
    }

    /**
     * Create a commercial for an account
     * @param accountId
     * @param commercialRequest
     * @return
     */
    @PostMapping("/{accountId}/add")
    public ResponseEntity<Object> addCommercial(@PathVariable int accountId, @RequestBody CommercialRequest commercialRequest) {
        log.info("Account create: Controller");
        CommercialResponseDTO commercialNewResponse = commercialService.createCommercial(accountId, commercialRequest);
        return ResponseUtil.generateSuccessResponse(commercialNewResponse, HttpStatus.CREATED, messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Update a commercial
     * @param accountId
     * @param commercialRequest
     * @return
     */
    @PutMapping("/{accountId}")
    public ResponseEntity<Object> updateCommercial(@PathVariable int accountId, @RequestBody CommercialRequest commercialRequest) {
        log.info("Account create: Controller");
        CommercialResponseDTO commercialNewResponse = commercialService.updateCommercial(accountId, commercialRequest);
        return ResponseUtil.generateSuccessResponse(commercialNewResponse, HttpStatus.CREATED, messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
    }

    /**
     * Get a commercial by account id
     * @param accountId
     * @return
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<Object> getCommercial(@PathVariable int accountId) {
        log.info("Account get: Controller");
        CommercialResponseDTO commercialNewResponse = commercialService.getCommercial(accountId);
        return ResponseUtil.generateSuccessResponse(commercialNewResponse, HttpStatus.OK, messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
    }
}
