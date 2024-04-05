package com.avensys.rts.accountservice.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.fasterxml.jackson.databind.JsonNode;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountRepositoryTest {
	

	@Autowired
	AccountRepository accountRepository;

	AccountEntity accountEntity;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
	JsonNode candidateSubmissionData;
	List<Long> createdByList;
	
	AccountEntity parentCompany;
	JsonNode accountSubmissionData;
	JsonNode commercialSubmissionData;
	

	@BeforeEach
	void setUp() {
		accountEntity = new AccountEntity(1,"name","markUp","msp",parentCompany,false,true,true,"accountNumber",1,createdAt,1,updatedAt,1,1,1,1,accountSubmissionData,commercialSubmissionData,"accountCountry");
	}

	@AfterEach
	void tearDown() throws Exception {
		accountRepository.deleteAll();
		accountEntity = null;
	}
	

	@Test
	void testFindByEntityTypeAndEntityId() {
		Optional<AccountEntity> candidateOptional = accountRepository
				.findByIdAndDeleted(1,false,true);
		assertNotNull(candidateOptional);
	}
	
	@Test
	void testFindByUserAndDraftAndDeleted() {
		Optional<AccountEntity> accountOptional =  accountRepository.findByUserAndDraftAndDeleted(1,false,false,true);
		assertNotNull(accountOptional);

	}
	
	@Test
	void testFindAllByUserAndDraftAndDeleted() {
		List<AccountEntity> accountList = accountRepository.findAllByUserAndDraftAndDeleted(1,false,false,true);
		assertNotNull(accountList);
	}
	
	@Test
	void testFindAllByUserAndDeleted() {
		List<AccountEntity> accountList = accountRepository.findAllByUserAndDeleted(1,false,true);
		assertNotNull(accountList);
	}
	
	@Test
	void testFindAllByUserIdsAndDeleted() {
		List<AccountEntity> accountList = accountRepository.findAllByUserIdsAndDeleted(createdByList,false ,true);
		assertNotNull(accountList);
	}
	
	@Test
	void testFindByIdAndDraft() {
		Optional<AccountEntity> accountOptional = accountRepository.findByIdAndDraft(1,false,true);
		assertNotNull(accountOptional);
	}

}
