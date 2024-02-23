package com.avensys.rts.accountservice.model;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.avensys.rts.accountservice.util.StringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: Koh He Xiang This is the model class include extra data for account
 * when sending back to client
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountExtraData {
	private Integer id;
	private String accountNumber;
	private String accountCountry;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String createdByName;
	private String updatedByName;

	public AccountExtraData(AccountEntity accountEntity) {
		this.id = accountEntity.getId();
		this.accountNumber = accountEntity.getAccountNumber();
		this.accountCountry = accountEntity.getAccountCountry();
		this.createdAt = accountEntity.getCreatedAt();
		this.updatedAt = accountEntity.getUpdatedAt();
	}

	// Use a Set to store all properties dynamically
	public Set<String> getAllFields() {
		Set<String> fields = new HashSet<>();
		for (Field field : this.getClass().getDeclaredFields()) {
			if (!field.canAccess(this)) {
				field.setAccessible(true);
			}
			fields.add(field.getName());
		}
		return fields;
	}

	// Get all field in map ( label and value)
	public List<HashMap<String, String>> getAllFieldsMap() {
		return getAllFields().stream().map(field -> {
			return new HashMap<String, String>() {
				{
					put("label", StringUtil.convertCamelCaseToTitleCase2(field));
					put("value", field);
				}
			};
		}).collect(Collectors.toList());
	}

	public JsonNode getSelectedFieldsJsonNode() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();

		// Use reflection to get all declared fields of AccountExtraData
		Field[] fields = AccountExtraData.class.getDeclaredFields();

		for (Field field : fields) {
			try {
				if (!field.canAccess(this)) {
					field.setAccessible(true);
				}

				// Add field to the JSON node
				jsonNode.put(field.getName(), String.valueOf(field.get(this)));
			} catch (IllegalAccessException e) {
				// Handle the exception as needed
				e.printStackTrace();
			}
		}
		return jsonNode;
	}

}
