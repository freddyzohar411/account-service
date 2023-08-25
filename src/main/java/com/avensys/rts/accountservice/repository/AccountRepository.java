package com.avensys.rts.accountservice.repository;

import com.avensys.rts.accountservice.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author: Koh He Xiang
 * This is the repository class for the account table in the database
 */
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
}
