package com.avensys.rts.accountservice.util;

import com.avensys.rts.accountservice.payloadnewrequest.FilterDTO;

import java.util.List;

public class QueryUtil {

	public final static String EQUAL = "Equal";
	public final static String NOT_EQUAL = "Not Equal";
	public final static String CONTAINS = "Contains";
	public final static String DOES_NOT_CONTAIN = "Does Not Contain";
	public final static String STARTS_WITH = "Starts With";
	public final static String ENDS_WITH = "Ends With";
	public final static String GREATER_THAN = "Greater Than";
	public final static String LESS_THAN = "Less Than";
	public final static String IS_EMPTY = "Is Empty";
	public final static String IS_NOT_EMPTY = "Is Not Empty";
	public final static String IS_TRUE = "Is True";
	public final static String IS_FALSE = "Is False";
	public final static String IS_NULL = "Is Null";
	public final static String IS_NOT_NULL = "Is Not Null";

	public final static String BEFORE = "Before";
	public final static String AFTER = "After";

	public static String buildQueryFromFilters(List<FilterDTO> filters) {
		if (filters == null || filters.isEmpty())
			return "";

		StringBuilder currentGroup = new StringBuilder();
		int parameterPosition = 1;

		for (int i = 0; i < filters.size(); i++) {
			FilterDTO filter = filters.get(i);
			String column = filter.getField();
			String condition = filter.getCondition();
			String value = filter.getValue();
			String operator = filter.getOperator();
			System.out.println("column: " + column);
			System.out.println("condition: " + condition);
			System.out.println("value: " + value);
			System.out.println("operator: " + operator);

			String conditionString = buildConditionString(column, condition, value, parameterPosition);

			if (i == 0) {
				currentGroup.append(conditionString);
			} else {
				currentGroup = new StringBuilder("(" + currentGroup + " " + operator + " " + conditionString + ")");
			}
			parameterPosition++;
		}

		return currentGroup.toString();
	}

	private static String buildConditionString(String column, String condition, String value, int parameterPosition) {
		StringBuilder conditionString = new StringBuilder();
//		String sqlOperator = getSqlCondition(condition);

		if (column.contains(".")) {
			String[] parts = column.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			switch (condition) {
			case EQUAL:
				conditionString.append(String.format("(%s->>'%s') = '%s'", jsonColumnName, jsonKey, value));
				break;
			case NOT_EQUAL:
				conditionString.append(String.format("(%s->>'%s') != '%s'", jsonColumnName, jsonKey, value));
				break;
			case CONTAINS:
				conditionString.append(String.format("(%s->>'%s') ILIKE '%s'", jsonColumnName, jsonKey, value));
				break;
			case DOES_NOT_CONTAIN:
				conditionString.append(String.format("(%s->>'%s') NOT ILIKE '%s'", jsonColumnName, jsonKey, value));
				break;
			case STARTS_WITH:
				conditionString.append(String.format("(%s->>'%s') ILIKE '%s%%'", jsonColumnName, jsonKey, value));
				break;
			case ENDS_WITH:
				conditionString.append(String.format("(%s->>'%s') ILIKE '%%%s'", jsonColumnName, jsonKey, value));
				break;
			case IS_EMPTY:
				conditionString.append(String.format("(%s->>'%s') IS NULL", jsonColumnName, jsonKey));
				break;
			case IS_NOT_EMPTY:
				conditionString.append(String.format("(%s->>'%s') IS NOT NULL", jsonColumnName, jsonKey));
				break;
			case GREATER_THAN:
				conditionString.append(
						String.format("CAST(NULLIF(%s->>'%s', '') AS DOUBLE PRECISION) > CAST(%s AS DOUBLE PRECISION)",
								jsonColumnName, jsonKey, value));
				break;
			case LESS_THAN:
				conditionString.append(
						String.format("CAST(NULLIF(%s->>'%s', '') AS DOUBLE PRECISION) < CAST(%s AS DOUBLE PRECISION)",
								jsonColumnName, jsonKey, value));
			}
		} else {
			switch (condition) {
			case EQUAL:
				conditionString.append(String.format("%s = '%s'", column, value));
				break;
			case NOT_EQUAL:
				conditionString.append(String.format("%s != '%s'", column, value));
				break;
			case CONTAINS:
				conditionString.append(String.format("%s ILIKE '%s'", column, value));
				break;
			case DOES_NOT_CONTAIN:
				conditionString.append(String.format("%s NOT ILIKE '%s'", column, value));
				break;
			case STARTS_WITH:
				conditionString.append(String.format("%s ILIKE '%s%%'", column, value));
				break;
			case ENDS_WITH:
				conditionString.append(String.format("%s ILIKE '%%%s'", column, value));
				break;
			case IS_EMPTY:
				conditionString.append(String.format("%s IS NULL", column));
				break;
			case IS_NOT_EMPTY:
				conditionString.append(String.format("%s IS NOT NULL", column));
				break;
			case GREATER_THAN:
				conditionString.append(String.format("CAST(NULLIF(%s, '') AS DOUBLE PRECISION) > CAST(%s AS DOUBLE PRECISION)",
						column, value));
				break;
			case LESS_THAN:
				conditionString.append(String.format("CAST(NULLIF(%s, '') AS DOUBLE PRECISION) < CAST(%s AS DOUBLE PRECISION)",
						column, value));
				break;
			}
		}
		return conditionString.toString();
	}

//	private static String getSqlCondition(String condition) {
//		switch (condition) {
//		case EQUAL:
//			return "=";
//		case NOT_EQUAL:
//			return "!=";
//		case CONTAINS:
//			return "ILIKE";
//		case DOES_NOT_CONTAIN:
//			return "NOT ILIKE";
//		case STARTS_WITH:
//			return "ILIKE";
//		case ENDS_WITH:
//			return "ILIKE";
//		case IS_EMPTY:
//			return "IS NULL";
//		case IS_NOT_EMPTY:
//			return "IS NOT NULL";
//		case GREATER_THAN:
//			return ">";
//		case LESS_THAN:
//			return "<";
//		case IS_TRUE:
//			return "IS TRUE";
//		case IS_FALSE:
//			return "IS FALSE";
//		default:
//			throw new IllegalArgumentException("Unknown condition: " + condition);
//		}
//	}

}
