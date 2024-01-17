package com.avensys.rts.accountservice.payloadnewresponse;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.avensys.rts.accountservice.entity.AccountEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountListingDataFlattenDTO {

	private Integer id;
	private String accountNumber;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String accountCountry;
	private String createdByName;
	private String updatedByName;
	// Use a Map to store all properties dynamically
	private Map<String, String> allProperties = new HashMap<>();

	public AccountListingDataFlattenDTO(AccountEntity accountEntity) {
		this.id = accountEntity.getId();
		this.accountNumber = accountEntity.getAccountNumber();
		this.createdAt = accountEntity.getCreatedAt();
		this.updatedAt = accountEntity.getUpdatedAt();
		this.accountCountry = accountEntity.getAccountCountry();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		JsonNode accountSubmissionDataNode = objectMapper.valueToTree(accountEntity.getAccountSubmissionData());
		flattenJsonNode("", accountSubmissionDataNode);
	}

	private void flattenJsonNode(String prefix, JsonNode node) {
		Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> entry = fields.next();
			String currentKey = prefix.isEmpty() ? entry.getKey() : prefix + "_" + entry.getKey();
			if (entry.getValue().isObject() || entry.getValue().isArray()) {
				flattenJsonNode(currentKey, entry.getValue());
			} else {
				// Populate the Map with dynamically generated keys
				allProperties.put(currentKey, entry.getValue().asText());
			}
		}
	}

	// Getter and setter methods for other fields...

	public Map<String, String> getAllProperties() {
		return allProperties;
	}

	public void setAllProperties(Map<String, String> allProperties) {
		this.allProperties = allProperties;
	}
}
