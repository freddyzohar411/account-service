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

import com.avensys.rts.accountservice.entity.CustomFieldsEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountCustomFieldsRepositoryTest {

	@Autowired
	AccountCustomFieldsRepository accountCustomFieldsRepository;

	CustomFieldsEntity customFieldsEntity;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;

	@BeforeEach
	void setUp() {
		customFieldsEntity = new CustomFieldsEntity(1L, "AccountCustomView", "Account", "ColumnNames", 1, createdAt, 1,
				updatedAt, false, false, true);
	}

	@AfterEach
	void tearDown() throws Exception {
		accountCustomFieldsRepository.deleteAll();
		customFieldsEntity = null;
	}

	@Test
	void testFindAllByUser() throws Exception {
		List<CustomFieldsEntity> CustomViewList = accountCustomFieldsRepository.findAllByUser(1, "Account", false);
		assertNotNull(CustomViewList);
	}

	@Test
	void testFindById() {
		Optional<CustomFieldsEntity> OptionalcustomEntity = accountCustomFieldsRepository.findById(1L);
		assertNotNull(OptionalcustomEntity);
	}

	@Test
	void testFindByIdAndDeleted() {
		Optional<CustomFieldsEntity> OptionalcustomEntity = accountCustomFieldsRepository.findByIdAndDeleted(1L, false,
				true);
		assertNotNull(OptionalcustomEntity);
	}

}
