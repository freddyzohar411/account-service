package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.APIClient.AddressAPIClient;
import com.avensys.rts.accountservice.APIClient.DocumentAPIClient;
import com.avensys.rts.accountservice.APIClient.IndustryAPIClient;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.payloadrequest.*;
import com.avensys.rts.accountservice.payloadresponse.*;
import com.avensys.rts.accountservice.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Koh He Xiang
 * This class is used to implement the AccountService interface and perform CRUD operations
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private AddressAPIClient addressAPIClient;

    @Autowired
    private IndustryAPIClient industryAPIClient;

    @Autowired
    private DocumentAPIClient documentAPIClient;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * This method is used to save account
     * Need to implement roll back if error occurs.
     * @param accountRequestDTO
     * @return
     */
    @Override
    @Transactional
    public AccountEntity createAccount(AccountRequestDTO accountRequestDTO) {
        // Logic to save account and relevant tables
        System.out.println("Saving account");
        AccountEntity accountEntity = new AccountEntity();
        AccountInformationDTO accountInformation = accountRequestDTO.getAccountInformation();
        LeadInformationDTO leadInformation = accountRequestDTO.getLeadInformation();
        AddressInformationDTO addressInformation = accountRequestDTO.getAddressInformation();

        // Set Account Information
        accountEntity.setRevenueCur(accountInformation.getRevenueCur());
        accountEntity.setRevenueAmt(accountInformation.getRevenueAmt());
        accountEntity.setName(accountInformation.getAccountName());
        accountEntity.setStatus(accountInformation.getAccountStatus());
        accountEntity.setRating(accountInformation.getAccountRating());
        accountEntity.setNoOfEmployees(accountInformation.getNoOfEmployees());
        accountEntity.setWebsite(accountInformation.getWebsite());
        accountEntity.setAccountSource(accountInformation.getAccountSource());
        accountEntity.setLandlineNumber(accountInformation.getLandlineNumber());
        accountEntity.setSecondaryOwner(accountInformation.getSecondaryOwner());
        accountEntity.setMsa(accountInformation.getMsa());
        accountEntity.setIndustry(accountInformation.getAccountIndustry());
        accountEntity.setSubIndustry(accountInformation.getSubIndustry());

        // Save parent company
        if (accountInformation.getParentCompany() != null) {
            AccountEntity parentAccount = accountRepository.findById(accountInformation.getParentCompany()).orElseThrow(
                    () -> new EntityNotFoundException("Parent company with %s not found".formatted(accountInformation.getParentCompany())));
            accountEntity.setParentCompany(parentAccount);
        }

        // Set lead Information
        accountEntity.setSalesName(leadInformation.getSalesName());
        accountEntity.setLeadSource(leadInformation.getLeadSource());
        accountEntity.setAccountName(leadInformation.getAccountName());

        // Set account remarks
        accountEntity.setRemarks(accountRequestDTO.getAccountRemarks());

        // Save account and get account id
        AccountEntity savedAccount = accountRepository.save(accountEntity);

        // Save Mailing address and Billing address (if same as mailing address)
        AddressRequestDTO addressRequestDTO = new AddressRequestDTO();
        addressRequestDTO.setLine1(addressInformation.getAddress().getLine1());
        addressRequestDTO.setLine2(addressInformation.getAddress().getLine2());
        addressRequestDTO.setLine3(addressInformation.getAddress().getLine3());
        addressRequestDTO.setCity(addressInformation.getAddress().getCity());
        addressRequestDTO.setCountry(addressInformation.getAddress().getCountry());
        addressRequestDTO.setPostalCode(addressInformation.getAddress().getPostalCode());
        addressRequestDTO.setType((short) 0);
        addressRequestDTO.setEntityId(savedAccount.getId());
        addressRequestDTO.setEntityType("account");
        try {
            HttpResponse mailAddressResponse = addressAPIClient.createAddress(addressRequestDTO);
            AddressResponseDTO mailAddressData = mapClientBodyToClass(mailAddressResponse.getData(), AddressResponseDTO.class);
            savedAccount.setAddress(mailAddressData.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error saving address." + e.getMessage());
        }

        // If same as mailing address is checked, then save Billing address
        if (addressInformation.getIsSameAsBillingAddress()) {
            addressRequestDTO.setType((short) 1);
            try {
                HttpResponse billingAddressResponse = addressAPIClient.createAddress(addressRequestDTO);
                AddressResponseDTO billingAddressData = mapClientBodyToClass(billingAddressResponse.getData(), AddressResponseDTO.class);
                savedAccount.setBillingAddress(billingAddressData.getId());
            } catch (Exception e) {
                throw new RuntimeException("Error saving billing address." + e.getMessage());
            }
        }

        // Save Billing address if not same as mailing address and billing address is not empty
        if (!addressInformation.getIsSameAsBillingAddress()) {
            addressRequestDTO.setLine1(addressInformation.getBillingAddress().getLine1());
            addressRequestDTO.setLine2(addressInformation.getBillingAddress().getLine2());
            addressRequestDTO.setLine3(addressInformation.getBillingAddress().getLine3());
            addressRequestDTO.setCity(addressInformation.getBillingAddress().getCity());
            addressRequestDTO.setCountry(addressInformation.getBillingAddress().getCountry());
            addressRequestDTO.setPostalCode(addressInformation.getBillingAddress().getPostalCode());
            addressRequestDTO.setType((short) 1);
            addressRequestDTO.setEntityId(savedAccount.getId());
            addressRequestDTO.setEntityType("account");
            try {
                HttpResponse billingAddressResponse = addressAPIClient.createAddress(addressRequestDTO);
                AddressResponseDTO billingAddressData = mapClientBodyToClass(billingAddressResponse.getData(), AddressResponseDTO.class);
                savedAccount.setBillingAddress(billingAddressData.getId());
            } catch (Exception e) {
                throw new RuntimeException("Error saving billing address." + e.getMessage());
            }
        }

        // Save Document
        if (accountInformation.getUploadAgreement() != null) {
            DocumentRequestDTO documentRequestDTO = new DocumentRequestDTO();
            documentRequestDTO.setEntityId(savedAccount.getId());
            documentRequestDTO.setType("agreement");
            documentRequestDTO.setFile(accountInformation.getUploadAgreement());
            HttpResponse documentResponse = documentAPIClient.createDocument(documentRequestDTO);
            DocumentResponseDTO documentData = mapClientBodyToClass(documentResponse.getData(), DocumentResponseDTO.class);
        }

        return accountRepository.save(accountEntity);
    }

    /**
     * This method is used to get all accounts
     * @return List<AccountResponseDTO>
     */
    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        List<AccountEntity> accountEntities = accountRepository.findAll();
        return accountEntities.stream().map(this::accountEntityToAccountResponseDTO).toList();
    }

    /**
     * This method is used to get all accounts
     * @return List<AccountEntity>
     */
    @Override
    public List<AccountEntity> getAllAccountsEntity() {
        return accountRepository.findAll();
    }

    /**
     * This method is used to get account by id
     * @param id
     * @return AccountResponseDTO
     */
    @Override
    public AccountResponseDTO getAccountById(int id) {
        AccountEntity accountEntity = accountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Account with %s not found".formatted(id))
        );
        return accountEntityToAccountResponseDTO(accountEntity);
    }

    /**
     * This method is used to update an account
     * @param accountRequest
     * @return AccountResponseDTO
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
    public void deleteAccount(int id) {
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
//        accountEntity.setParentCompany(23); // Need to key from company
        accountEntity.setWebsite(accountInformation.getWebsite());
        accountEntity.setAccountSource(accountInformation.getAccountSource());
        accountEntity.setLandlineCountry(accountInformation.getLandlineCountry());
        accountEntity.setLandlineNumber(accountInformation.getLandlineNumber());
        accountEntity.setSecondaryOwner(accountInformation.getSecondaryOwner());
        accountEntity.setMsa(accountInformation.getMsa()); // Not in Account
        accountEntity.setSalesName(leadInformation.getSalesName());
        accountEntity.setLeadSource(leadInformation.getLeadSource());
        accountEntity.setAccountName(leadInformation.getAccountName());
        accountEntity.setAddress(2); // Address microservice
        accountEntity.setBillingAddress(3); // Address microservice
        accountEntity.setRemarks(accountRequestDTO.getAccountRemarks());
        accountEntity.setMsp(30.0);
        accountEntity.setMarkup(20.0);
        return accountEntity;
    }

    private <T> T mapClientBodyToClass(Object body, Class<T> mappedDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.convertValue(body, mappedDTO);
    }

    /**
     * This method is used to convert AccountEntity to AccountInformationDTO
     *
     * @param accountEntity
     * @return AccountInformationDTO
     */
    private AccountInformationResponseDTO accountEntityToAccountInformationDTO(AccountEntity accountEntity) {
        AccountInformationResponseDTO accountInformationDTO = new AccountInformationResponseDTO();
        accountInformationDTO.setAccountName(accountEntity.getName());
        accountInformationDTO.setAccountStatus(accountEntity.getStatus());
        accountInformationDTO.setAccountRating(accountEntity.getRating());
        accountInformationDTO.setAccountIndustry(accountEntity.getIndustry());
        accountInformationDTO.setSubIndustry(accountEntity.getSubIndustry());
        accountInformationDTO.setNoOfEmployees(accountEntity.getNoOfEmployees());
        accountInformationDTO.setRevenueAmt(accountEntity.getRevenueAmt());
        accountInformationDTO.setRevenueCur(accountEntity.getRevenueCur());
        if (accountEntity.getParentCompany() != null) {
            accountInformationDTO.setParentCompany(accountEntity.getParentCompany().getId());
        }
        accountInformationDTO.setWebsite(accountEntity.getWebsite());
        accountInformationDTO.setAccountSource(accountEntity.getAccountSource());
        accountInformationDTO.setLandlineCountry(accountEntity.getLandlineCountry());
        accountInformationDTO.setLandlineNumber(accountEntity.getLandlineNumber());
        accountInformationDTO.setSecondaryOwner(accountEntity.getSecondaryOwner());
        accountInformationDTO.setMsa(accountEntity.getMsa());
        return accountInformationDTO;
    }

    /**
     * This method is used to convert AccountEntity to LeadInformationDTO
     *
     * @param accountEntity
     * @return LeadInformationDTO
     */
    private LeadInformationResponseDTO accountEntityToLeadInformationDTO(AccountEntity accountEntity) {
        LeadInformationResponseDTO leadInformationDTO = new LeadInformationResponseDTO();
        leadInformationDTO.setSalesName(accountEntity.getSalesName());
        leadInformationDTO.setLeadSource(accountEntity.getLeadSource());
        leadInformationDTO.setAccountName(accountEntity.getAccountName());
        return leadInformationDTO;
    }

    /**
     * Internal method to convert AccountEntity to AddressInformationDTO
     *
     * @param accountEntity
     * @return
     */
    private AddressInformationResponseDTO accountEntityToAddressInformationDTO(AccountEntity accountEntity) {
        //Address Information
        AddressInformationResponseDTO addressInformation = new AddressInformationResponseDTO();
        AddressResponseDTO mailingAddressData = mapClientBodyToClass(addressAPIClient.getAddressById(accountEntity.getAddress()).getData(), AddressResponseDTO.class);
        addressInformation.setAddress(mailingAddressData);
        if (accountEntity.getBillingAddress() != null) {
            AddressResponseDTO billingAddressData = mapClientBodyToClass(addressAPIClient.getAddressById(accountEntity.getBillingAddress()).getData(), AddressResponseDTO.class);
            addressInformation.setBillingAddress(billingAddressData);
        }
        return addressInformation;
    }

    /**
     * Internal method to convert Account Entity to AddressResponseDTO
     *
     * @param accountEntity
     * @return
     */
    private AccountResponseDTO accountEntityToAccountResponseDTO(AccountEntity accountEntity) {
        AccountInformationResponseDTO accountInformation = accountEntityToAccountInformationDTO(accountEntity);

        //Lead Information
        LeadInformationResponseDTO leadInformation = accountEntityToLeadInformationDTO(accountEntity);

        //Address Information
        AddressInformationResponseDTO addressInformation = accountEntityToAddressInformationDTO(accountEntity);

        //Account Remarks
        String accountRemarks = accountEntity.getRemarks();

        //Account Response DTO
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
        accountResponseDTO.setId(accountEntity.getId());
        accountResponseDTO.setAccountInformation(accountInformation);
        accountResponseDTO.setLeadInformation(leadInformation);
        accountResponseDTO.setAddressInformation(addressInformation);
        accountResponseDTO.setAccountRemarks(accountRemarks);
        return accountResponseDTO;
    }
}
