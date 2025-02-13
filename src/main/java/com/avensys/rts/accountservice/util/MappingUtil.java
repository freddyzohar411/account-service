package com.avensys.rts.accountservice.util;

import aj.org.objectweb.asm.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MappingUtil {

	/**
	 * This method is used to convert Object to Class Use to convert API client
	 * Httpresponse back to DTO class
	 *
	 * @param body
	 * @param mappedDTO <T>
	 * @return T
	 */
	public static <T> T mapClientBodyToClass(Object body, Class<T> mappedDTO) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper.convertValue(body, mappedDTO);
	}

	/**
	 * This method is used to convert JSONString to JsonNode
	 * 
	 * @param jsonString
	 * @return
	 */
	public static JsonNode convertJSONStringToJsonNode(String jsonString) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		try {
			return objectMapper.readTree(jsonString);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method is used to convert JsonNode to JSONString
	 * 
	 * @param jsonNode
	 * @return
	 */
	public static String convertJsonNodeToJSONString(JsonNode jsonNode) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		try {
			return objectMapper.writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method is used to convert Object to List of JsonNode (Used in HTTPResponse)
	 * @param objList
	 * @param key
	 * @return
	 */
	public static List<JsonNode> convertObjectToListOfJsonNode(List<Object> objList, String key) {
		List<JsonNode> JsonNodeList = objList.stream().map(obj -> {
			if (obj instanceof Map) {
				// Assuming workExperience is a Map
				Object submissionData = ((Map<?, ?>) obj).get(key);
				// Check if submissionData is a String
				if (submissionData instanceof String) {
					try {
						ObjectMapper objectMapper = new ObjectMapper();
						return objectMapper.readTree((String) submissionData);
					} catch (IOException e) {
						// Handle the exception, e.g., log an error
						e.printStackTrace();
						return null; // or throw an exception, or handle it according to your requirements
					}
				}
			}
			return null; // or throw an exception, or handle it according to your requirements
		}).filter(Objects::nonNull) // Remove null entries
				.toList();
		return JsonNodeList;
	}

	/**
	 * This method is used to convert Object to JsonNode (Used in HTTPResponse)
	 * @param obj
	 * @param key
	 * @return
	 */
	public static JsonNode convertObjectToJsonNode(Object obj, String key) {
		if (obj instanceof Map) {
			// Assuming workExperience is a Map
			Object submissionData = ((Map<?, ?>) obj).get(key);
			// Check if submissionData is a String
			if (submissionData instanceof String) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					return objectMapper.readTree((String) submissionData);
				} catch (IOException e) {
					// Handle the exception, e.g., log an error
					e.printStackTrace();
					return null; // or throw an exception, or handle it according to your requirements
				}
			}
		}
		return null; // or throw an exception, or handle it according to your requirements
	}

	/**
	 * This method is used to merge List of JSON nodes into one
	 * @param jsonNodes
	 * @return
	 */
	public static JsonNode mergeJsonNodes(List<JsonNode> jsonNodes) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode mergedNode = mapper.createObjectNode();

		for (JsonNode node : jsonNodes) {
			if (node.isObject()) {
				mergedNode.setAll((ObjectNode) node);
			} else {
				throw new IllegalArgumentException("Only JSON objects can be merged.");
			}
		}
		return mergedNode;
	}
   public static <T> List<T> convertJsonNodeToList(JsonNode jsonNode, Class<T> elementType) {
	   ObjectMapper mapper = new ObjectMapper();
		TypeFactory typeFactory = mapper.getTypeFactory();
		JavaType listType = typeFactory.constructCollectionType(List.class, elementType);
		return mapper.convertValue(jsonNode, listType);
	}
}
