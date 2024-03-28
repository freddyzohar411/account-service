package com.avensys.rts.accountservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.accountservice.entity.CustomFieldsEntity;
import com.avensys.rts.accountservice.payloadnewresponse.CustomFieldsResponseDTO;

public interface AccountCustomFieldsRepository extends JpaRepository<CustomFieldsEntity, Long> {

	public Boolean existsByName(String name);
	
    @Query(value = "SELECT c FROM customView c WHERE c.createdBy = ?1")
    List<CustomFieldsEntity> findAllByUser(Integer userId);
    
    @Query(value = "SELECT c FROM customView c WHERE c.createdBy = ?1 AND c.isSelected = ?2")
    CustomFieldsResponseDTO findAllByUserAndSelected(Integer userId,boolean isSelected);
    
    //Optional<CustomFieldsEntity> findById(Long id);
}
