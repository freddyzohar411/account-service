package com.avensys.rts.accountservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.entity.CustomFieldsEntity;
import com.avensys.rts.accountservice.payloadnewresponse.CustomFieldsResponseDTO;

public interface AccountCustomFieldsRepository extends JpaRepository<CustomFieldsEntity, Long> {

	public Boolean existsByName(String name);
	
    @Query(value = "SELECT c FROM customView c WHERE c.createdBy = ?1 AND c.type = ?2 AND c.isDeleted = ?3")
    List<CustomFieldsEntity> findAllByUser(Integer userId,String type,boolean isDeleted);
    
    @Query(value = "SELECT c FROM customView c WHERE c.id = ?1")
    Optional<CustomFieldsEntity> findById(Long id);
    
    //for checkeing is there is any isSelected true.
    @Query(value = "SELECT c FROM customView c WHERE c.createdBy = ?1 AND c.type = ?2")
    CustomFieldsEntity findByUserAndType(Integer userId,String type);
    
    @Query(value = "SELECT c FROM customView c WHERE c.createdBy = ?1 AND c.isSelected = ?2")
    CustomFieldsResponseDTO findAllByUserAndSelected(Integer userId,boolean isSelected);
    
    @Query(value = "SELECT c FROM customView c WHERE c.id = ?1 AND c.isDeleted = ?2 AND c.isActive = ?3")
    Optional<CustomFieldsEntity> findByIdAndDeleted(Long id, boolean isDeleted, boolean isActive);
}
