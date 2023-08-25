package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.payload.*;
import com.avensys.rts.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Koh He Xiang
 * This class is used to implement the AccountService interface and perform CRUD operations
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * This method is used to save account
     * @param accountRequestDTO
     * @return
     */
    @Override
    public AccountEntity createAccount(AccountRequestDTO accountRequestDTO) {
        System.out.println("Saving account");
        try {
            AccountEntity ac = accountRepository.save(ToAccountEntity(accountRequestDTO));
            System.out.println(ac.getName());
            return accountRepository.save(ac);
        } catch (Exception e) {
            throw new RuntimeException("Error saving account");
        }
    }

    /**
     * This method is used to get all accounts
     * @return
     */
    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        List<AccountEntity> accountEntities = accountRepository.findAll();
        List<AccountResponseDTO> accountResponseDTOS = null;
        if (accountEntities.isEmpty()) {
            return null;
        }

        // Logic to aggregate data from other tables to form AccountResponseDTO

        return null;
    }

    /**
     * This method is used to get account by id
     * @param id
     * @return
     */
    @Override
    public AccountResponseDTO getAccountById(Long id) {
        Optional<AccountEntity> accountEntity = accountRepository.findById(id);
        // Logic to aggregate data from other tables to form AccountResponseDTO

        return null;
    }

    /**
     * This method is used to update an account
     * @param accountRequest
     * @return
     */
    @Override
    public AccountResponseDTO updateAccount(AccountRequestDTO accountRequest) {
        // Logic to update account and relevant tables
        return null;
    }

    /**
     * This method is used to delete an account
     * @param id
     */
    @Override
    public void deleteAccount(Long id) {
        Optional<AccountEntity> accountEntity = accountRepository.findById(id);
        // Logic to delete account and dependent tables
    }

    private AccountEntity ToAccountEntity(AccountRequestDTO accountRequestDTO) {
        AccountEntity accountEntity = new AccountEntity();
        AccountInformationDTO accountInformation = accountRequestDTO.getAccountInformation();
        LeadInformationDTO leadInformation = accountRequestDTO.getLeadInformation();
        AddressInformationDTO addressInformation = accountRequestDTO.getAddressInformation();

        accountEntity.setName(accountInformation.getAccountName());
        accountEntity.setStatus(accountInformation.getAccountStatus());
        accountEntity.setRating(accountInformation.getAccountRating());
        accountEntity.setIndustry(10);  // Need to key from industry
        accountEntity.setSubIndustry(12);  // Need to key from industry
        accountEntity.setNoOfEmployees(accountInformation.getNoOfEmployees());
        accountEntity.setRevenueAmt(accountInformation.getRevenueAmt());
        accountEntity.setRevenueCur(1);  // Need to key from currency
        accountEntity.setParentCompany(23); // Need to key from company
        accountEntity.setWebsite(accountInformation.getWebsite());
        accountEntity.setAccountSource(accountInformation.getAccountSource());
        accountEntity.setLandlineCountry(accountInformation.getLandlineCountry());
        accountEntity.setLandlineNumber(accountInformation.getLandlineNumber());
        accountEntity.setSecondaryOwner(accountInformation.getSecondaryOwner());
        accountEntity.setMsa(accountInformation.getMsa()); // Not in Account
        accountEntity.setSalesName(leadInformation.getSalesName());
        accountEntity.setLeadSource(leadInformation.getLeadSource());
        accountEntity.setAccountName(leadInformation.getAccountName());
        accountEntity.setAddress(2);
        accountEntity.setBillingAddress(3);
        accountEntity.setRemarks(accountRequestDTO.getAccountRemarks());
        accountEntity.setMsp(30.0);
        accountEntity.setMarkup(20.0);
        return accountEntity;
    }
}
