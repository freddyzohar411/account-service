package com.avensys.rts.accountservice.repository;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.entity.AccountNewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountNewRepository extends JpaRepository<AccountNewEntity, Integer> {
    @Query(value = "SELECT a FROM accountNew a WHERE a.id = ?1 AND a.isDeleted = ?2")
    Optional<AccountNewEntity> findByIdAndDeleted(int id, boolean isDeleted);

    @Query(value = "SELECT a FROM accountNew a WHERE a.createdBy = ?1 AND a.isDraft = ?2 AND a.isDeleted = ?3")
    Optional<AccountNewEntity> findByUserAndDraftAndDeleted(Integer userId, boolean draft, boolean deleted);

}
