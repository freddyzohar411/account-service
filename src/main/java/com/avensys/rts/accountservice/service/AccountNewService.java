package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.payloadnewrequest.AccountNewRequestDTO;
import com.avensys.rts.accountservice.payloadnewrequest.CommercialNewRequest;
import com.avensys.rts.accountservice.payloadnewresponse.AccountNewResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.CommercialNewResponseDTO;
import com.avensys.rts.accountservice.payloadrequest.AccountRequestDTO;
import com.avensys.rts.accountservice.payloadresponse.AccountNameReponseDTO;
import com.avensys.rts.accountservice.payloadresponse.AccountResponseDTO;

import java.util.List;

public interface AccountNewService {
    AccountResponseDTO createAccount(AccountNewRequestDTO accountRequest);

    AccountNewResponseDTO getAccount(Integer id);

    AccountNewResponseDTO getAccountIfDraft();

    AccountNewResponseDTO updateAccount(Integer id, AccountNewRequestDTO accountRequest);

    /**
     * This method is used to get all accounts name
     * @return
     */
    List<AccountNameReponseDTO> getAllAccountsName();


}
