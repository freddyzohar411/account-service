package com.avensys.rts.accountservice.repository;

import com.avensys.rts.accountservice.entity.AccountNewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomAccountRepository {
    Page<AccountNewEntity> findAllByOrderBy(Integer userId, Boolean isDeleted, Boolean isDraft, Pageable pageable);
    Page<AccountNewEntity> findAllByOrderByString(Integer userId, Boolean isDeleted, Boolean isDraft, Pageable pageable);
    Page<AccountNewEntity> findAllByOrderByNumeric(Integer userId, Boolean isDeleted, Boolean isDraft, Pageable pageable);
    Page<AccountNewEntity> findAllByOrderByAndSearchString(Integer userId, Boolean isDeleted, Boolean isDraft, Pageable pageable, List<String> searchFields, String searchTerm);
    Page<AccountNewEntity> findAllByOrderByAndSearchNumeric(Integer userId, Boolean isDeleted, Boolean isDraft, Pageable pageable, List<String> searchFields, String searchTerm);

}
