package com.avensys.rts.accountservice.controller;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.payload.AccountRequestDTO;
import com.avensys.rts.accountservice.service.AccountServiceImpl;
import com.avensys.rts.accountservice.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

//        @GetMapping("/account")
//    public ResponseEntity<HttpResponse> getAccount(HttpServletRequest request){
////        String threadId = (String) request.getAttribute("threadId");
//        HttpResponse httpResponse = new HttpResponse();
//        String greeting = messageSource.getMessage("msg.text", null, Locale.US);
//        httpResponse.setData(new MessageDTO(greeting));
//        return ResponseEntity.ok(httpResponse);
//    }

    @PostMapping("/accounts")
    public ResponseEntity<Object> addAccount(@Valid @RequestBody AccountRequestDTO accountRequest) {
        System.out.println("In saving account now...");
        AccountEntity account = accountService.createAccount(accountRequest);
        return ResponseUtil.generateSuccessResponse(account, HttpStatus.CREATED, messageSource.getMessage("account.created", null, LocaleContextHolder.getLocale()));
    }
}
