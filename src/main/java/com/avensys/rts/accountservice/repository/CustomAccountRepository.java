package com.avensys.rts.accountservice.repository;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.payloadnewrequest.FilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomAccountRepository {
	Page<AccountEntity> findAllByOrderBy(Integer userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable);

	Page<AccountEntity> findAllByOrderByString(Integer userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable);

	Page<AccountEntity> findAllByOrderByNumeric(Integer userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable);

	Page<AccountEntity> findAllByOrderByAndSearchString(Integer userId, Boolean isDeleted, Boolean isDraft,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm);

	Page<AccountEntity> findAllByOrderByAndSearchNumeric(Integer userId, Boolean isDeleted, Boolean isDraft,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm);

	List<AccountEntity> getAllAccountsNameWithSearch(String query, Integer userId, Boolean isDeleted, Boolean isDraft);

	// Check only user id
	Page<AccountEntity> findAllByOrderByStringWithUserIds(List<Long> userIds, Boolean isDeleted, Boolean isDraft,
			Boolean isActive, List<String> accountOwnerValues, Pageable pageable, List<FilterDTO> filters);

	Page<AccountEntity> findAllByOrderByNumericWithUserIds(List<Long> userIds, Boolean isDeleted, Boolean isDraft,
			Boolean isActive, List<String> accountOwnerValue, Pageable pageable, List<FilterDTO> filters);

	Page<AccountEntity> findAllByOrderByAndSearchStringWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isDraft, Boolean isActive, List<String> accountOwnerValue, Pageable pageable,
			List<String> searchFields, String searchTerm, List<FilterDTO> filters);

	Page<AccountEntity> findAllByOrderByAndSearchNumericWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isDraft, Boolean isActive, List<String> accountOwnerValue, Pageable pageable,
			List<String> searchFields, String searchTerm, List<FilterDTO> filters);
}
