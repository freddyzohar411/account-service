package com.avensys.rts.accountservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.accountservice.entity.CustomFieldsEntity;

public interface AccountCustomFieldsRepository extends JpaRepository<CustomFieldsEntity, Long> {

	// Boolean existsByCustomViewName(String name);
}
