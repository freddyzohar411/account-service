package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.APIClient.AddressAPIClient;
import com.avensys.rts.accountservice.APIClient.DocumentAPIClient;
import com.avensys.rts.accountservice.APIClient.UserAPIClient;
import com.avensys.rts.accountservice.constant.MessageConstants;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.exception.RequiredDocumentMissingException;
import com.avensys.rts.accountservice.payloadrequest.*;
import com.avensys.rts.accountservice.payloadresponse.*;
import com.avensys.rts.accountservice.repository.AccountRepository;
import com.avensys.rts.accountservice.util.JwtUtil;
import com.avensys.rts.accountservice.util.MappingUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Autowired
    private UserAPIClient userAPIClient;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * This method is used to save account
     * Roll back occur if one of the save fails
     *
     * @param accountRequestDTO
     * @return
     */
    @Override
    @Transactional
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {

        // Test getting information from JWT
        System.out.println("Email: " + JwtUtil.getEmailFromContext());
        System.out.println("Roles: " + JwtUtil.getRolesFromContext());

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

        // Set an account number
        accountEntity.setAccountNumber("A" + RandomStringUtils.randomNumeric(7));

        // Set created by User foreign key
        String email = JwtUtil.getEmailFromContext();
        HttpResponse userResponse = userAPIClient.getUserByEmail(email);
        UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
        accountEntity.setCreatedBy(userData.getId());

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
     *
     * @return List<AccountResponseDTO>
     */
    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        List<AccountEntity> accountEntities = accountRepository.findAllAndDeleted(false);
        return accountEntities.stream().map(this::accountEntityToAccountResponseDTO).toList();
    }

    /**
     * This method is used to get all accounts and map it to account name DTO
     *
     * @return List<AccountNameReponseDTO>
     */
    @Override
    public List<AccountNameReponseDTO> getAllAccountsName() {
        List<AccountEntity> accountEntities = accountRepository.findAllByUserAndDeleted(getUserId(), false);
        return accountEntities.stream().map(this::accountEntityToAccountNameResponseDTO).toList();
    }

    /**
     * This method is used to get all accounts
     *
     * @return List<AccountEntity>
     */
    @Override
    public List<AccountEntity> getAllAccountsEntity() {
        return accountRepository.findAll();
    }

    /**
     * This method is used to get account by id
     *
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
     *
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
            HttpResponse mailAddressResponse = addressAPIClient.updateAddress(accountFound.getAddress(), mailingAddressRequest);
            AddressResponseDTO mailAddressData = MappingUtil.mapClientBodyToClass(mailAddressResponse.getData(), AddressResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error saving address." + e.getMessage());
        }

        // If same as mailing address is checked, then Update Billing address
        if (addressInformation.getIsSameAsBillingAddress()) {
            AddressRequestDTO billingAddressRequest = accountRequestToAddressRequest(addressInformation.getAddress(), 1, MessageConstants.ACCOUNT_TYPE, accountFound.getId());
            try {
                HttpResponse billingAddressResponse = addressAPIClient.updateAddress(accountFound.getBillingAddress(), billingAddressRequest);
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

        // Update account updated by
        accountFound.setUpdatedBy(getUserId());

        // Save updated account
        AccountEntity accountUpdated = accountRepository.save(accountFound);

        return accountEntityToAccountResponseDTO(accountUpdated);
    }

    /**
     * This method is used to soft delete an account by Id
     *
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
     *
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
        accountEntity.setDraft(false);
        accountEntity.setUpdatedBy(getUserId());
        AccountEntity accountUpdated = accountRepository.save(accountEntity);

        return accountEntityToCommercialResponse(accountUpdated);
    }

    /**
     * This method is used to get the account commercial
     *
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
     * This method is used to get the account if draft
     *
     * @return
     */
    @Override
    public AccountResponseDTO getAccountIfDraft() {
        // Get User id
        Optional<AccountEntity> accountFound = accountRepository.findByUserAndDraftAndDeleted(getUserId(), true, false);
        return accountFound.map(this::accountEntityToAccountResponseDTO).orElse(null);
    }

    /**
     * This method is used to get the accounts by pagination and Sort
     * @param page
     * @param size
     * @param sortBy
     * @param sortDir
     * @return AccountListingResponseDTO
     */
    @Override
    public AccountListingResponseDTO getAllAccountsByPaginationAndSort(Integer page, Integer size, String sortBy, String sortDir) {
        System.out.println("Sorting");
        Sort sort = null;
        if (sortBy != null) {
            // Get direction based on sort direction
            Sort.Direction direction = Sort.DEFAULT_DIRECTION;
            if (sortDir != null) {
                direction = sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            }
            sort = Sort.by(direction, sortBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        }

        Pageable pageable = null;
        if (page == null && size == null) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
        } else {
            pageable = PageRequest.of(page, size, sort);
        }
        Page<AccountEntity> accountsPage = accountRepository.findAllByPaginationAndSort(getUserId(), false, false, pageable);

        return accountPageToAccountListingDTO(accountsPage);
    }

    /**
     * This method is used to get the accounts by pagination and Sort and Search
     * @param page
     * @param size
     * @param sortBy
     * @param sortDir
     * @param searchTerm
     * @return AccountListingResponseDTO
     */
    @Override
    public AccountListingResponseDTO getAllAccountsByPaginationAndSortAndSearch(Integer page, Integer size, String sortBy, String sortDir, String searchTerm) {
        System.out.println("Sorting With Search");
        Sort sort = null;
        if (sortBy != null) {
            // Get direction based on sort direction
            Sort.Direction direction = Sort.DEFAULT_DIRECTION;
            if (sortDir != null) {
                direction = sortDir.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            }
            sort = Sort.by(direction, sortBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        }

        Pageable pageable = null;
        if (page == null && size == null) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
        } else {
            pageable = PageRequest.of(page, size, sort);
        }

        // Dynamic search based on custom view (future feature)
        List<String> customView = List.of("name", "salesName", "accountSource", "accountNumber", "status", "parentCompanyName");
        Specification<AccountEntity> specification = (root, query, criteriaBuilder) -> {
            // Old and possible
//            Predicate[] searchPredicates = customView.stream()
//                    .map(field -> criteriaBuilder.like(root.get(field), "%" + searchTerm + "%"))
//                    .toArray(Predicate[]::new);
//
//            Predicate searchOrPredicates = criteriaBuilder.or(searchPredicates);
//            List<Predicate> predicates = new ArrayList<>();
//
//            Predicate isDraftPredicate = criteriaBuilder.equal(root.get("isDraft"), false);
//            Predicate isDeletedPredicate = criteriaBuilder.equal(root.get("isDeleted"), false);
//            // get only for user id
//            Predicate createdByPredicate = criteriaBuilder.equal(root.get("createdBy"), getUserId());
//
//            return criteriaBuilder.and(searchOrPredicates, criteriaBuilder.and(isDraftPredicate, isDeletedPredicate, createdByPredicate));

            List<Predicate> predicates = new ArrayList<>();

            // Custom fields you want to search in
            for (String field : customView) {
                if ("parentCompanyName".equals(field)) {
                    Join<AccountEntity, AccountEntity> parentJoin = root.join("parentCompany", JoinType.LEFT);
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(parentJoin.get("name")), "%" + searchTerm.toLowerCase() + "%"));
                } else {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + searchTerm.toLowerCase() + "%"));
                }
            }
            Predicate searchOrPredicates = criteriaBuilder.or(predicates.toArray(new Predicate[0]));

            Predicate isDraftPredicate = criteriaBuilder.equal(root.get("isDraft"), false);
            Predicate isDeletedPredicate = criteriaBuilder.equal(root.get("isDeleted"), false);
            Predicate createdByPredicate = criteriaBuilder.equal(root.get("createdBy"), getUserId());

            return criteriaBuilder.and(searchOrPredicates, isDraftPredicate, isDeletedPredicate, createdByPredicate);
        };

        Page<AccountEntity> accountsPage = accountRepository.findAll(specification, pageable);

        return accountPageToAccountListingDTO(accountsPage);
    }

    /**
     * This method is used to get account information only by id
     * @param accountId
     * @return
     */
    @Override
    public AccountInformationResponseDTO getAccountInformationById(int accountId) {
        AccountEntity accountEntity = accountRepository.findByIdAndDeleted(accountId, false).orElseThrow(
                () -> new EntityNotFoundException("Account with %s not found".formatted(accountId))
        );
        return accountEntityToAccountInformationDTO(accountEntity);
    }

    /**
     * This method is used to get all accounts by user id
     * @param accountsPage
     * @return
     */
    private AccountListingResponseDTO accountPageToAccountListingDTO(Page<AccountEntity> accountsPage) {
        List<AccountUserResponseDTO> accountUserResponseList = accountsPage.getContent().stream().map(this::accountEntityToAccountUserResponseDTO).toList();
        AccountListingResponseDTO accountListingResponseDTO = new AccountListingResponseDTO();
        accountListingResponseDTO.setAccounts(accountUserResponseList);
        accountListingResponseDTO.setPage(accountsPage.getNumber());
        accountListingResponseDTO.setPageSize(accountsPage.getSize());
        accountListingResponseDTO.setTotalElements(accountsPage.getTotalElements());
        accountListingResponseDTO.setTotalPages(accountsPage.getTotalPages());
        return accountListingResponseDTO;
    }

    /**
     * Internal method to convert AccountEntity to AccountNameReponseDTO
     *
     * @param accountEntity
     * @return
     */
    private AccountNameReponseDTO accountEntityToAccountNameResponseDTO(AccountEntity accountEntity) {
        AccountNameReponseDTO accountNameReponseDTO = new AccountNameReponseDTO();
        accountNameReponseDTO.setId(accountEntity.getId());
        accountNameReponseDTO.setName(accountEntity.getName());
        return accountNameReponseDTO;
    }

    private CommercialResponseDTO accountEntityToCommercialResponse(AccountEntity accountEntity) {
        CommercialResponseDTO commercialResponseDTO = new CommercialResponseDTO();
        commercialResponseDTO.setMarkUp(accountEntity.getMarkup());
        commercialResponseDTO.setMsp(accountEntity.getMsp());
        return commercialResponseDTO;
    }

    /**
     * This method is used to convert AccountInformationDTO to AccountEntity
     *
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
     *
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
     *
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
        return addressRequestDTO;
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
        accountInformationDTO.setSalesName(accountEntity.getSalesName());
        accountInformationDTO.setAccountStatus(accountEntity.getStatus());
        accountInformationDTO.setAccountRating(accountEntity.getRating());
        accountInformationDTO.setAccountIndustry(accountEntity.getIndustry());
        accountInformationDTO.setSubIndustry(accountEntity.getSubIndustry());
        accountInformationDTO.setNoOfEmployees(accountEntity.getNoOfEmployees());
        accountInformationDTO.setRevenueAmt(accountEntity.getRevenueAmt());
        accountInformationDTO.setRevenueCur(accountEntity.getRevenueCur());
        accountInformationDTO.setParentCompanyEntity(accountEntity.getParentCompany());
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
        accountResponseDTO.setAccountNumber(accountEntity.getAccountNumber());
        return accountResponseDTO;
    }

    /**
     * Internal method to convert Account Entity to AccountUserResponseDTO
     *
     * @param accountEntity
     * @return
     */
    private AccountUserResponseDTO accountEntityToAccountUserResponseDTO(AccountEntity accountEntity) {
        AccountUserResponseDTO accountUserResponseDTO = new AccountUserResponseDTO();
        AccountResponseDTO accountResponseDTO = accountEntityToAccountResponseDTO(accountEntity);
        accountUserResponseDTO.setAccount(accountResponseDTO);

        // Get user information from user id
        HttpResponse userResponse = userAPIClient.getUserById(accountEntity.getCreatedBy());
        UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
        accountUserResponseDTO.setUser(userData);
        return accountUserResponseDTO;
    }

    private Integer getUserId() {
        String email = JwtUtil.getEmailFromContext();
        HttpResponse userResponse = userAPIClient.getUserByEmail(email);
        UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
        return userData.getId();
    }
}
