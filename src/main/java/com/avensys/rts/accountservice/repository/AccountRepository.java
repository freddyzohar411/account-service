package com.avensys.rts.accountservice.repository;

import com.avensys.rts.accountservice.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * author: Koh He Xiang
 * This is the repository class for the account table in the database
 */
public interface AccountRepository extends JpaRepository<AccountEntity, Integer>, JpaSpecificationExecutor<AccountEntity> {
        Optional<AccountEntity> findByName(String name);

        @Query(value = "SELECT a FROM account a WHERE a.id = ?1 AND a.isDeleted = ?2")
        Optional<AccountEntity> findByIdAndDeleted(int id, boolean isDeleted);

        @Query(value = "SELECT a FROM account a WHERE a.isDeleted = ?1")
        List<AccountEntity> findAllAndDeleted(boolean isDeleted);

        @Query(value = "SELECT a FROM account a WHERE a.createdBy = ?1 AND a.isDeleted = ?2")
        List<AccountEntity> findAllByUserAndDeleted(Integer createdBy, boolean isDeleted);

        @Query(value = "SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM account a WHERE a.name = ?1 AND a.isDeleted = false")
        boolean existByName(String name);

        @Query(value = "SELECT a FROM account a WHERE a.isDraft = ?1 AND a.isDeleted = ?2")
        Optional<AccountEntity> findByDraftAndDeleted(boolean draft, boolean deleted);

        @Query(value = "SELECT a FROM account a WHERE a.createdBy = ?1 AND a.isDraft = ?2 AND a.isDeleted = ?3")
        Optional<AccountEntity> findByUserAndDraftAndDeleted(Integer userId, boolean draft, boolean deleted);

        @Query(value = "SELECT a FROM account a WHERE a.createdBy = ?1 AND a.isDraft = ?2 AND a.isDeleted = ?3")
        Page<AccountEntity> findAllByPaginationAndSort(Integer createdBy, boolean draft, boolean deleted, Pageable pageable);

        Page<AccountEntity> findAll(Specification<AccountEntity> specification, Pageable pageable);
}