package com.avensys.rts.accountservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.entity.CustomFieldsEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer>, CustomAccountRepository {
    @Query(value = "SELECT a FROM account a WHERE a.id = ?1 AND a.isDeleted = ?2 AND a.isActive = ?3")
    Optional<AccountEntity> findByIdAndDeleted(int id, boolean isDeleted, boolean isActive);

    @Query(value = "SELECT a FROM account a WHERE a.createdBy = ?1 AND a.isDraft = ?2 AND a.isDeleted = ?3 AND a.isActive = ?4")
    Optional<AccountEntity> findByUserAndDraftAndDeleted(Integer userId, boolean draft, boolean deleted, boolean isActive);

    @Query(value = "SELECT a FROM account a WHERE a.createdBy = ?1 AND a.isDraft = ?2 AND a.isDeleted = ?3 AND a.isActive = ?4")
    List<AccountEntity> findAllByUserAndDraftAndDeleted(Integer userId, boolean draft, boolean deleted, boolean isActive);

    @Query(value = "SELECT a FROM account a WHERE a.isDraft = ?1 AND a.isDeleted = ?2 AND a.isActive = ?3")
    List<AccountEntity> findAllByIsDraftAndIsDeletedAndIsActive(boolean isDraft, boolean isDeleted, boolean isActive);

    @Query(value = "SELECT a FROM account a WHERE a.createdBy = ?1 AND a.isDeleted = ?2 AND a.isActive = ?3")
    List<AccountEntity> findAllByUserAndDeleted(Integer createdBy, boolean isDeleted, boolean isActive);

    @Query(value = "SELECT a FROM account a WHERE a.createdBy IN (?1) AND a.isDeleted = ?2 AND a.isActive = ?3")
    List<AccountEntity> findAllByUserIdsAndDeleted(List<Long> createdByList, boolean isDeleted, boolean isActive);

    @Query(value = "SELECT a FROM account a WHERE a.id = ?1 AND a.isDraft = ?2 AND a.isActive = ?3")
    Optional<AccountEntity> findByIdAndDraft(Integer id, boolean draft, boolean isActive);

    @Query(value = "SELECT a FROM account a WHERE a.isDraft = ?1 AND a.isDeleted = ?2 AND a.isActive = ?3")
    Optional<AccountEntity> findByAllByIsDraftAndIsDeletedAndIsActive(boolean draft, boolean isDeleted, boolean isActive);

}
