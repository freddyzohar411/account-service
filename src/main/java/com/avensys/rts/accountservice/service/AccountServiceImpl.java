package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.APIClient.AddressAPIClient;
import com.avensys.rts.accountservice.APIClient.DocumentAPIClient;
import com.avensys.rts.accountservice.APIClient.IndustryAPIClient;
import com.avensys.rts.accountservice.constant.MessageConstants;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.exception.DuplicateResourceException;
import com.avensys.rts.accountservice.exception.RequiredDocumentMissingException;
import com.avensys.rts.accountservice.payloadrequest.*;
import com.avensys.rts.accountservice.payloadresponse.*;
import com.avensys.rts.accountservice.repository.AccountRepository;
import com.avensys.rts.accountservice.util.MappingUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final String ACTIVE_STATUS = "active";
    private final String ACCOUNT_TYPE = "account";

    private final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private AddressAPIClient addressAPIClient;

    @Autowired
    private DocumentAPIClient documentAPIClient;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * This method is used to save account
     * Roll back occur if one of the save fails
     * @param accountRequestDTO
     * @return
     */
    @Override
    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {

        // Logic to save account and relevant tables
        AccountEntity accountEntity = new AccountEntity();
        AccountInformationDTO accountInformation = accountRequestDTO.getAccountInformation();
        LeadInformationDTO leadInformation = accountRequestDTO.getLeadInformation();
        AddressInformationDTO addressInformation = accountRequestDTO.getAddressInformation();

        // Set Account Information
        accountInformationDTOToAccountEntity(accountInformation, accountEntity);

        // Set lead Information
        leadInformationDTOToAccountEntity(leadInformation, accountEntity);

        // Set account remarks
        accountEntity.setRemarks(accountRequestDTO.getAccountRemarks());

        // Save account and get account id
        AccountEntity savedAccount = accountRepository.save(accountEntity);

        // Save Mailing address and Billing address (if same as mailing address)
        AddressRequestDTO mailingAddressRequest = accountRequestToAddressRequest(addressInformation.getAddress(), 0, MessageConstants.ACCOUNT_TYPE, savedAccount.getId());
        try {
            HttpResponse mailAddressResponse = addressAPIClient.createAddress(mailingAddressRequest);
            AddressResponseDTO mailAddressData = MappingUtil.mapClientBodyToClass(mailAddressResponse.getData(), AddressResponseDTO.class);
            savedAccount.setAddress(mailAddressData.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error saving address." + e.getMessage());
        }

        // If same as mailing address is checked, then save Billing address
        if (addressInformation.getIsSameAsBillingAddress()) {
            AddressRequestDTO billingAddressRequest = accountRequestToAddressRequest(addressInformation.getAddress(), 1, MessageConstants.ACCOUNT_TYPE, savedAccount.getId());
            try {
                HttpResponse billingAddressResponse = addressAPIClient.createAddress(billingAddressRequest);
                AddressResponseDTO billingAddressData = MappingUtil.mapClientBodyToClass(billingAddressResponse.getData(), AddressResponseDTO.class);
                savedAccount.setBillingAddress(billingAddressData.getId());
            } catch (Exception e) {
                throw new RuntimeException("Error saving billing address." + e.getMessage());
            }
        }

        // Save Billing address if not same as mailing address
        if (!addressInformation.getIsSameAsBillingAddress()) {
            AddressRequestDTO billingAddressRequest = accountRequestToAddressRequest(addressInformation.getBillingAddress(), 1, MessageConstants.ACCOUNT_TYPE, savedAccount.getId());
            try {
                HttpResponse billingAddressResponse = addressAPIClient.createAddress(billingAddressRequest);
                AddressResponseDTO billingAddressData = MappingUtil.mapClientBodyToClass(billingAddressResponse.getData(), AddressResponseDTO.class);
                savedAccount.setBillingAddress(billingAddressData.getId());
            } catch (Exception e) {
                throw new RuntimeException("Error saving billing address." + e.getMessage());
            }
        }

        // Save Document
        if (accountInformation.getUploadAgreement() != null) {
            DocumentRequestDTO documentRequestDTO = new DocumentRequestDTO();
            // Save document and tag to account entity
            documentRequestDTO.setEntityId(savedAccount.getId());
            documentRequestDTO.setEntityType(ACCOUNT_TYPE);

            documentRequestDTO.setFile(accountInformation.getUploadAgreement());
            HttpResponse documentResponse = documentAPIClient.createDocument(documentRequestDTO);
            DocumentResponseDTO documentData = MappingUtil.mapClientBodyToClass(documentResponse.getData(), DocumentResponseDTO.class);
        } else {
            throw new RequiredDocumentMissingException("Upload agreement document is required");
        }

        AccountEntity accountSaved = accountRepository.save(savedAccount);
        log.info("Account saved successfully");
        return accountEntityToAccountResponseDTO(accountSaved);
    }

    /**
     * This method is used to get all accounts
     * @return List<AccountResponseDTO>
     */
    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        List<AccountEntity> accountEntities = accountRepository.findAllAndDeleted(false);
        return accountEntities.stream().map(this::accountEntityToAccountResponseDTO).toList();
    }

    /**
     * This method is used to get all accounts and map it to account name DTO
     * @return List<AccountNameReponseDTO>
     */
    @Override
    public List<AccountNameReponseDTO> getAllAccountsName() {
        List<AccountEntity> accountEntities = accountRepository.findAllAndDeleted(false);
        return accountEntities.stream().map(this::accountEntityToAccountNameResponseDTO).toList();
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
     * @param accountId
     * @return AccountResponseDTO
     */
    @Override
    public AccountResponseDTO getAccountById(int accountId) {
        AccountEntity accountEntity = accountRepository.findByIdAndDeleted(accountId, false).orElseThrow(
                () -> new EntityNotFoundException("Account with %s not found".formatted(accountId))
        );
        return accountEntityToAccountResponseDTO(accountEntity);
    }

    /**
     * This method is used to update an account
     * @param accountRequestDTO
     * @return AccountResponseDTO
     */
    @Override
    public AccountResponseDTO updateAccount(int accountId, AccountRequestDTO accountRequestDTO) {
        // Find an account by id
        AccountEntity accountFound = accountRepository.findByIdAndDeleted(accountId, false).orElseThrow(
                () -> new EntityNotFoundException("Account with %s not found".formatted(accountId))
        );

        // Logic Update account and relevant tables
        AccountInformationDTO accountInformation = accountRequestDTO.getAccountInformation();
        LeadInformationDTO leadInformation = accountRequestDTO.getLeadInformation();
        AddressInformationDTO addressInformation = accountRequestDTO.getAddressInformation();

        // Set updated Account Information
        accountInformationDTOToAccountEntity(accountInformation, accountFound);

        // Set updated lead Information
        leadInformationDTOToAccountEntity(leadInformation, accountFound);

        // Set updated account remarks
        accountFound.setRemarks(accountRequestDTO.getAccountRemarks());

        // Update Mailing address and Billing address (if same as mailing address)
        AddressRequestDTO mailingAddressRequest = accountRequestToAddressRequest(addressInformation.getAddress(), 0, MessageConstants.ACCOUNT_TYPE, accountFound.getId());
        try {
            HttpResponse mailAddressResponse = addressAPIClient.updateAddress(accountFound.getAddress(),mailingAddressRequest);
            AddressResponseDTO mailAddressData = MappingUtil.mapClientBodyToClass(mailAddressResponse.getData(), AddressResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving address." + e.getMessage());
        }

        // If same as mailing address is checked, then Update Billing address
        if (addressInformation.getIsSameAsBillingAddress()) {
            AddressRequestDTO billingAddressRequest = accountRequestToAddressRequest(addressInformation.getAddress(), 1, MessageConstants.ACCOUNT_TYPE, accountFound.getId());
            try {
                HttpResponse billingAddressResponse = addressAPIClient.updateAddress(accountFound.getBillingAddress(),billingAddressRequest);
                AddressResponseDTO billingAddressData = MappingUtil.mapClientBodyToClass(billingAddressResponse.getData(), AddressResponseDTO.class);
            } catch (Exception e) {
                throw new RuntimeException("Error saving billing address." + e.getMessage());
            }
        }

        // Update Billing address if not same as mailing address
        if (!addressInformation.getIsSameAsBillingAddress()) {
            AddressRequestDTO billingAddressRequest = accountRequestToAddressRequest(addressInformation.getBillingAddress(), 1, MessageConstants.ACCOUNT_TYPE, accountFound.getId());
            try {
                HttpResponse billingAddressResponse = addressAPIClient.updateAddress(accountFound.getBillingAddress(), billingAddressRequest);
                AddressResponseDTO billingAddressData = MappingUtil.mapClientBodyToClass(billingAddressResponse.getData(), AddressResponseDTO.class);
            } catch (Exception e) {
                throw new RuntimeException("Error saving billing address." + e.getMessage());
            }
        }

        // Update Document
        if (accountInformation.getUploadAgreement() != null) {
            DocumentRequestDTO documentRequestDTO = new DocumentRequestDTO();
            documentRequestDTO.setEntityType(ACCOUNT_TYPE);
            documentRequestDTO.setFile(accountInformation.getUploadAgreement());
            documentRequestDTO.setEntityId(accountFound.getId());
            HttpResponse documentResponse = documentAPIClient.updateDocument(documentRequestDTO);
            DocumentResponseDTO documentData = MappingUtil.mapClientBodyToClass(documentResponse.getData(), DocumentResponseDTO.class);
        } else {
            HttpResponse documentResponse = documentAPIClient.getDocumentByEntityTypeAndId("account", accountFound.getId());
        }

        AccountEntity accountUpdated = accountRepository.save(accountFound);

        return accountEntityToAccountResponseDTO(accountUpdated);
    }

    /**
     * This method is used to soft delete an account by Id
     * @param id
     */
    @Override
    public void deleteAccountById(int id) {
        AccountEntity accountEntity = accountRepository.findByIdAndDeleted(id, false).orElseThrow(
                () -> new EntityNotFoundException("Account with %s not found".formatted(id))
        );

        // Soft delete account
        accountEntity.setDeleted(true);
        accountRepository.save(accountEntity);
//        // Hard Delete - Logic to delete account and dependent tables
//
//        // Delete Mailing address
//        if (accountEntity.getAddress() != null) {
//            HttpResponse mailAddressResponse = addressAPIClient.deleteAddressById(accountEntity.getAddress());
//        }
//        // Delete Billing address
//        if (accountEntity.getBillingAddress() != null) {
//            HttpResponse billingAddressResponse = addressAPIClient.deleteAddressById(accountEntity.getBillingAddress());
//        }
//        // Delete Document
//        DocumentDeleteRequestDTO documentDeleteRequestDTO = new DocumentDeleteRequestDTO();
//        documentDeleteRequestDTO.setEntityId(accountEntity.getId());
//        documentDeleteRequestDTO.setType("agreement");
//        documentAPIClient.deleteDocumentByEntityIdAndType(documentDeleteRequestDTO);
//
//        // Delete Account
//        accountRepository.deleteById(id);
    }

    /**
     * This method is used to set the account commercial
     * @param accountId
     * @param commercialRequest
     * @return
     */
    @Override
    public CommercialResponseDTO setAccountCommercial(int accountId, CommercialRequestDTO commercialRequest) {
        AccountEntity accountEntity = accountRepository.findByIdAndDeleted(accountId, false).orElseThrow(
                () -> new EntityNotFoundException("Account with %s not found".formatted(accountId))
        );

        accountEntity.setMarkup(commercialRequest.getMarkUp());
        accountEntity.setMsp(commercialRequest.getMsp());
        accountEntity.setStatus(ACTIVE_STATUS);
        AccountEntity accountUpdated = accountRepository.save(accountEntity);

        return accountEntityToCommercialResponse(accountUpdated);
    }

    /**
     * This method is used to get the account commercial
     * @param accountId
     * @return
     */
    @Override
    public CommercialResponseDTO getAccountCommercialById(int accountId) {
        AccountEntity accountEntity = accountRepository.findByIdAndDeleted(accountId, false).orElseThrow(
                () -> new EntityNotFoundException("Account commericial Id %s not found".formatted(accountId))
        );
        return accountEntityToCommercialResponse(accountEntity);
    }

    /**
     * Internal method to convert AccountEntity to AccountNameReponseDTO
     * @param accountEntity
     * @return
     */
    private AccountNameReponseDTO accountEntityToAccountNameResponseDTO(AccountEntity accountEntity) {
        AccountNameReponseDTO accountNameReponseDTO = new AccountNameReponseDTO();
        accountNameReponseDTO.setId(accountEntity.getId());
        accountNameReponseDTO.setName(accountEntity.getName());
        return accountNameReponseDTO;
    }

    private CommercialResponseDTO accountEntityToCommercialResponse(AccountEntity accountEntity){
        CommercialResponseDTO commercialResponseDTO = new CommercialResponseDTO();
        commercialResponseDTO.setMarkUp(accountEntity.getMarkup());
        commercialResponseDTO.setMsp(accountEntity.getMsp());
        return commercialResponseDTO;
    }

    /**
     * This method is used to convert AccountInformationDTO to AccountEntity
     * @param accountInformationDTO
     * @param accountEntity
     */
    private void accountInformationDTOToAccountEntity(AccountInformationDTO accountInformationDTO, AccountEntity accountEntity) {
        accountEntity.setName(accountInformationDTO.getAccountName());
        accountEntity.setSalesName(accountInformationDTO.getSalesName());
        accountEntity.setStatus(accountInformationDTO.getAccountStatus());
        accountEntity.setRating(accountInformationDTO.getAccountRating());
        accountEntity.setIndustry(accountInformationDTO.getAccountIndustry());
        accountEntity.setSubIndustry(accountInformationDTO.getSubIndustry());
        accountEntity.setNoOfEmployees(accountInformationDTO.getNoOfEmployees());
        accountEntity.setRevenueAmt(accountInformationDTO.getRevenueAmt());
        accountEntity.setRevenueCur(accountInformationDTO.getRevenueCur());
        if (accountInformationDTO.getParentCompany() != null && accountInformationDTO.getParentCompany() > 0) {
            AccountEntity parentAccount = accountRepository.findById(accountInformationDTO.getParentCompany()).orElseThrow(
                    () -> new EntityNotFoundException("Parent company with %s not found".formatted(accountInformationDTO.getParentCompany())));
            accountEntity.setParentCompany(parentAccount);
        }
        accountEntity.setWebsite(accountInformationDTO.getWebsite());
        accountEntity.setAccountSource(accountInformationDTO.getAccountSource());
        accountEntity.setLandlineCountry(accountInformationDTO.getLandlineCountry());
        accountEntity.setLandlineNumber(accountInformationDTO.getLandlineNumber());
        accountEntity.setSecondaryOwner(accountInformationDTO.getSecondaryOwner());
        accountEntity.setMsa(accountInformationDTO.getMsa());
    }

    /**
     * This method is used to convert LeadInformationDTO to AccountEntity
     * @param leadInformationDTO
     * @param accountEntity
     */
    private void leadInformationDTOToAccountEntity(LeadInformationDTO leadInformationDTO, AccountEntity accountEntity) {
        accountEntity.setLeadSalesName(leadInformationDTO.getSalesName());
        accountEntity.setLeadSource(leadInformationDTO.getLeadSource());
        accountEntity.setAccountName(leadInformationDTO.getAccountName());
    }

    /**
     * This method is used to convert AddressDTO to AddressRequestDTO
     * @param addressRequest
     * @param type
     * @param EntityType
     * @param entityId
     * @return
     */
    private AddressRequestDTO accountRequestToAddressRequest(AddressDTO addressRequest, int type, String EntityType, int entityId) {
        AddressRequestDTO addressRequestDTO = new AddressRequestDTO();
        addressRequestDTO.setLine1(addressRequest.getLine1());
        addressRequestDTO.setLine2(addressRequest.getLine2());
        addressRequestDTO.setLine3(addressRequest.getLine3());
        addressRequestDTO.setCity(addressRequest.getCity());
        addressRequestDTO.setCountry(addressRequest.getCountry());
        addressRequestDTO.setPostalCode(addressRequest.getPostalCode());
        addressRequestDTO.setType((short) type);
        addressRequestDTO.setEntityId(entityId);
        addressRequestDTO.setEntityType(EntityType);
        return  addressRequestDTO;
    }

    /**
     * This method is used to convert AccountEntity to AccountInformationDTO
     * @param accountEntity
     * @return AccountInformationDTO
     */
    private AccountInformationResponseDTO accountEntityToAccountInformationDTO(AccountEntity accountEntity) {
        AccountInformationResponseDTO accountInformationDTO = new AccountInformationResponseDTO();
        accountInformationDTO.setAccountName(accountEntity.getName());
        accountInformationDTO.setSalesName(accountEntity.getSalesName());
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
        leadInformationDTO.setSalesName(accountEntity.getLeadSalesName());
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
        AddressResponseDTO mailingAddressData = MappingUtil.mapClientBodyToClass(addressAPIClient.getAddressById(accountEntity.getAddress()).getData(), AddressResponseDTO.class);
        addressInformation.setAddress(mailingAddressData);
        if (accountEntity.getBillingAddress() != null) {
            AddressResponseDTO billingAddressData = MappingUtil.mapClientBodyToClass(addressAPIClient.getAddressById(accountEntity.getBillingAddress()).getData(), AddressResponseDTO.class);
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
