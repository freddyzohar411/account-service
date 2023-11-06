package com.avensys.rts.accountservice.repository;

import com.avensys.rts.accountservice.entity.AccountNewEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.hibernate.query.NativeQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class CustomAccountRepositoryImpl implements CustomAccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<AccountNewEntity> findAllByOrderBy(Integer userId, Boolean isDeleted, Boolean isDraft, Boolean isActive, Pageable pageable) {
        String sortBy = pageable.getSort().get().findFirst().get().getProperty();
        // Determine if sortBy is a regular column or a JSONB column
        String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty() : "updated_at";
        if (sortBy.contains(".")) {  // assuming sortBy is in the format "jsonColumn.jsonKey"
            String[] parts = sortBy.split("\\.");
            String jsonColumnName = parts[0];
            String jsonKey = parts[1];
//            orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
            orderByClause = String.format(
                    "CAST(%s->>'%s' AS INTEGER)",
                    jsonColumnName, jsonKey
            );
        }

        // Extract sort direction from pageable
        String sortDirection = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getDirection().name() : "ASC";

        // Build the complete query string
        String queryString = String.format(
                "SELECT * FROM account_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive ORDER BY %s %s NULLS LAST",
                orderByClause, sortDirection);

        // Create and execute the query
        Query query = entityManager.createNativeQuery(queryString, AccountNewEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("isDeleted", isDeleted);
        query.setParameter("isDraft", isDraft);
        query.setParameter("isActive", isActive);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Get the result list
        List<AccountNewEntity> resultList = query.getResultList();

        // Build the count query string
        String countQueryString = "SELECT COUNT(*) FROM account_new WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive";

        // Create and execute the count query
        Query countQuery = entityManager.createNativeQuery(countQueryString);
        countQuery.setParameter("userId", userId);
        countQuery.setParameter("isDeleted", isDeleted);
        countQuery.setParameter("isDraft", isDraft);
        countQuery.setParameter("isActive", isActive);
        Long countResult = ((Number) countQuery.getSingleResult()).longValue();

        // Create and return a Page object
        return new PageImpl<>(resultList, pageable, countResult);
    }


    @Override
    public Page<AccountNewEntity> findAllByOrderByString(Integer userId, Boolean isDeleted, Boolean isDraft, Boolean isActive, Pageable pageable) {
        String sortBy = pageable.getSort().get().findFirst().get().getProperty();
        // Determine if sortBy is a regular column or a JSONB column
        String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty() : "updated_at";
        if (sortBy.contains(".")) {  // assuming sortBy is in the format "jsonColumn.jsonKey"
            String[] parts = sortBy.split("\\.");
            String jsonColumnName = parts[0];
            String jsonKey = parts[1];
            orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
        }

        // Extract sort direction from pageable
        String sortDirection = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getDirection().name() : "ASC";

        // Build the complete query string
        String queryString = String.format(
                "SELECT * FROM account_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive ORDER BY %s %s NULLS LAST",
                orderByClause, sortDirection);

        // Create and execute the query
        Query query = entityManager.createNativeQuery(queryString, AccountNewEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("isDeleted", isDeleted);
        query.setParameter("isDraft", isDraft);
        query.setParameter("isActive", isActive);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Get the result list
        List<AccountNewEntity> resultList = query.getResultList();

        // Build the count query string
        String countQueryString = "SELECT COUNT(*) FROM account_new WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive";

        // Create and execute the count query
        Query countQuery = entityManager.createNativeQuery(countQueryString);
        countQuery.setParameter("userId", userId);
        countQuery.setParameter("isDeleted", isDeleted);
        countQuery.setParameter("isDraft", isDraft);
        countQuery.setParameter("isActive", isActive);
        Long countResult = ((Number) countQuery.getSingleResult()).longValue();

        // Create and return a Page object
        return new PageImpl<>(resultList, pageable, countResult);
    }

    @Override
    public Page<AccountNewEntity> findAllByOrderByNumeric(Integer userId, Boolean isDeleted, Boolean isDraft, Boolean isActive, Pageable pageable) {
        String sortBy = pageable.getSort().get().findFirst().get().getProperty();
        // Determine if sortBy is a regular column or a JSONB column
        String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty() : "updated_at";
        if (sortBy.contains(".")) {  // assuming sortBy is in the format "jsonColumn.jsonKey"
            String[] parts = sortBy.split("\\.");
            String jsonColumnName = parts[0];
            String jsonKey = parts[1];
            orderByClause = String.format(
                    "CAST(NULLIF(%s->>'%s', '') AS INTEGER)",
                    jsonColumnName, jsonKey
            );
        }

        // Extract sort direction from pageable
        String sortDirection = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getDirection().name() : "ASC";

        // Build the complete query string
        String queryString = String.format(
                "SELECT * FROM account_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive ORDER BY %s %s NULLS LAST",
                orderByClause, sortDirection);

        // Log the generated SQL (for debugging)
        System.out.println(queryString);

        // Create and execute the query
        Query query = entityManager.createNativeQuery(queryString, AccountNewEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("isDeleted", isDeleted);
        query.setParameter("isDraft", isDraft);
        query.setParameter("isActive", isActive);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Get the result list
        List<AccountNewEntity> resultList = query.getResultList();

        // Build the count query string
        String countQueryString = "SELECT COUNT(*) FROM account_new WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive";

        // Create and execute the count query
        Query countQuery = entityManager.createNativeQuery(countQueryString);
        countQuery.setParameter("userId", userId);
        countQuery.setParameter("isDeleted", isDeleted);
        countQuery.setParameter("isDraft", isDraft);
        countQuery.setParameter("isActive", isActive);
        Long countResult = ((Number) countQuery.getSingleResult()).longValue();

        // Create and return a Page object
        return new PageImpl<>(resultList, pageable, countResult);
    }


    @Override
    public Page<AccountNewEntity> findAllByOrderByAndSearchString(Integer userId, Boolean isDeleted, Boolean isDraft, Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm) {

        // Determine if sortBy is a regular column or a JSONB column
        String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty() : "updated_at";
        String orderByClause;
        if (sortBy.contains(".")) {  // assuming sortBy is in the format "jsonColumn.jsonKey"
            String[] parts = sortBy.split("\\.");
            String jsonColumnName = parts[0];
            String jsonKey = parts[1];
            orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
        } else {
            orderByClause = sortBy;
        }

        // Extract sort direction from pageable
        String sortDirection = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getDirection().name() : "ASC";

        // Build the dynamic search conditions based on searchFields
        StringBuilder searchConditions = new StringBuilder();
        for (int i = 0; i < searchFields.size(); i++) {
            String field = searchFields.get(i);
            if (field.contains(".")) {  // assuming field is in the format "jsonColumn.jsonKey"
                String[] parts = field.split("\\.");
                String jsonColumnName = parts[0];
                String jsonKey = parts[1];
                searchConditions.append(String.format(" OR (%s->>'%s') ILIKE :searchTerm ", jsonColumnName, jsonKey));
            } else {
                searchConditions.append(String.format(" OR CAST(%s AS TEXT) ILIKE :searchTerm ", field));
//                searchConditions.append(String.format(" OR %s ILIKE :searchTerm ", field));
            }
        }

        // Remove the leading " OR " from the searchConditions
        if (searchConditions.length() > 0) {
            searchConditions.delete(0, 4);
        }

        // Build the complete query string
        String queryString = String.format(
                "SELECT * FROM account_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND (%s) ORDER BY %s %s NULLS LAST",
                searchConditions.toString(),
                orderByClause,
                sortDirection);

        // Create and execute the query
        Query query = entityManager.createNativeQuery(queryString, AccountNewEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("isDeleted", isDeleted);
        query.setParameter("isDraft", isDraft);
        query.setParameter("isActive", isActive);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Get the result list
        List<AccountNewEntity> resultList = query.getResultList();

        // Build the count query string
        String countQueryString = String.format(
                "SELECT COUNT(*) FROM account_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND (%s)",
                searchConditions.toString());

        // Create and execute the count query
        Query countQuery = entityManager.createNativeQuery(countQueryString);
        countQuery.setParameter("userId", userId);
        countQuery.setParameter("isDeleted", isDeleted);
        countQuery.setParameter("isDraft", isDraft);
        countQuery.setParameter("isActive", isActive);
        countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
        Long countResult = ((Number) countQuery.getSingleResult()).longValue();

        // Create and return a Page object
        return new PageImpl<>(resultList, pageable, countResult);
    }

    @Override
    public Page<AccountNewEntity> findAllByOrderByAndSearchNumeric(Integer userId, Boolean isDeleted, Boolean isDraft,Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm) {

        // Determine if sortBy is a regular column or a JSONB column
        String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty() : "updated_at";
        String orderByClause;
        if (sortBy.contains(".")) {  // assuming sortBy is in the format "jsonColumn.jsonKey"
            String[] parts = sortBy.split("\\.");
            String jsonColumnName = parts[0];
            String jsonKey = parts[1];
            orderByClause = String.format(
                    "CAST(NULLIF(%s->>'%s', '') AS INTEGER)",
                    jsonColumnName, jsonKey
            );
        } else {
            orderByClause = sortBy;
        }

        // Extract sort direction from pageable
        String sortDirection = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getDirection().name() : "ASC";

        // Build the dynamic search conditions based on searchFields
        StringBuilder searchConditions = new StringBuilder();
        for (int i = 0; i < searchFields.size(); i++) {
            String field = searchFields.get(i);
            if (field.contains(".")) {  // assuming field is in the format "jsonColumn.jsonKey"
                String[] parts = field.split("\\.");
                String jsonColumnName = parts[0];
                String jsonKey = parts[1];
                searchConditions.append(String.format(" OR (%s->>'%s') ILIKE :searchTerm ", jsonColumnName, jsonKey));
            } else {
                searchConditions.append(String.format(" OR CAST(%s AS TEXT) ILIKE :searchTerm ", field));
//                searchConditions.append(String.format(" OR %s ILIKE :searchTerm ", field));
            }
        }

        // Remove the leading " OR " from the searchConditions
        if (searchConditions.length() > 0) {
            searchConditions.delete(0, 4);
        }

        // Build the complete query string
        String queryString = String.format(
                "SELECT * FROM account_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND (%s) ORDER BY %s %s NULLS LAST",
                searchConditions.toString(),
                orderByClause,
                sortDirection);

        // Create and execute the query
        Query query = entityManager.createNativeQuery(queryString, AccountNewEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("isDeleted", isDeleted);
        query.setParameter("isDraft", isDraft);
        query.setParameter("isActive", isActive);
        query.setParameter("searchTerm", "%" + searchTerm + "%");
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // Get the result list
        List<AccountNewEntity> resultList = query.getResultList();

        // Build the count query string
        String countQueryString = String.format(
                "SELECT COUNT(*) FROM account_new WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND (%s)",
                searchConditions.toString());

        // Create and execute the count query
        Query countQuery = entityManager.createNativeQuery(countQueryString);
        countQuery.setParameter("userId", userId);
        countQuery.setParameter("isDeleted", isDeleted);
        countQuery.setParameter("isDraft", isDraft);
        countQuery.setParameter("isActive", isActive);
        countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
        Long countResult = ((Number) countQuery.getSingleResult()).longValue();

        // Create and return a Page object
        return new PageImpl<>(resultList, pageable, countResult);
    }

    @Override
    public List<AccountNewEntity> getAllAccountsNameWithSearch(String query, Integer userId, Boolean isDeleted, Boolean isDraft) {
        String sql = "SELECT * FROM account_new WHERE (created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft) AND";

        int parameterPosition = 1; // Initialize parameter position
        // Operator allowed in query string: =, !=, >, <, >=, <=
        String regex = "([\\w.]+)([><]=?|!=|=)([\\w.]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);

        while (matcher.find()) {
            String fieldName = matcher.group(1);
            String operator = matcher.group(2);
            String value = matcher.group(3);

            sql += " ";

            if (fieldName.contains(".")) {
                String[] parts = fieldName.split("\\.");
                String jsonColumnName = parts[0];
                String jsonKey = parts[1];
                sql += String.format("(%s->>'%s') ILIKE :param%d AND", jsonColumnName, jsonKey, parameterPosition);
            } else {
                sql += String.format("%s ILIKE :param%d AND", fieldName, parameterPosition);
            }
            parameterPosition++; // Increment parameter position for the next parameter
        }

        sql = sql.replaceAll("AND$", ""); // Remove trailing "AND"

        System.out.println("SQL Query: " + sql);

        NativeQuery<AccountNewEntity> nativeQuery = (NativeQuery<AccountNewEntity>) entityManager.createNativeQuery(sql, AccountNewEntity.class);

        // Set parameters for user isdeleted and isdraft
        nativeQuery.setParameter("userId", userId);
        nativeQuery.setParameter("isDeleted", isDeleted);
        nativeQuery.setParameter("isDraft", isDraft);

        // Set parameters for dynamic search fields
        parameterPosition = 1; // Reset parameter position
        // Bind parameters
        matcher = pattern.matcher(query); // Re-initialize matcher
        while (matcher.find()) {
            String value = matcher.group(3);
            nativeQuery.setParameter("param" + parameterPosition, "%" + value.toLowerCase() + "%"); // Convert to lowercase for case-insensitive comparison
            parameterPosition++; // Increment parameter position for the next parameter
        }

        return nativeQuery.getResultList();
    }

}