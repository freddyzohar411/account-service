package com.avensys.rts.accountservice.aspect;

import com.avensys.rts.accountservice.APIClient.UserAPIClient;
import com.avensys.rts.accountservice.annotation.RequiresAllPermissions;
import com.avensys.rts.accountservice.annotation.RequiresAnyPermission;
import com.avensys.rts.accountservice.constant.MessageConstants;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.enums.Permission;
import com.avensys.rts.accountservice.exception.PermissionDeniedException;
import com.avensys.rts.accountservice.payloadresponse.UserResponseDTO;
import com.avensys.rts.accountservice.util.JwtUtil;
import com.avensys.rts.accountservice.util.MappingUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Author: Koh He Xiang
 * Aspect to check if the user has the required permission (Both all and any)
 */
@Aspect
@Component
public class SecurityPermissionAspect {

    private final Logger log = LoggerFactory.getLogger(SecurityPermissionAspect.class);

    @Autowired
    private UserAPIClient userAPIClient;

    @Autowired
    private MessageSource messageSource;

    /**
     * Check if the user has all the required permissions
     * @param requiresPermission
     */
    @Before("@annotation(requiresPermission)")
    public void checkAllPermission(RequiresAllPermissions requiresPermission) {
        System.out.println("Hello from RequiresAllPermissions");
        List<String> requiredPermissions = Arrays.stream(requiresPermission.value()).map(Permission::toString).toList();
        requiredPermissions.forEach(System.out::println);

        if (!requiredPermissions.isEmpty()) {
            // Logic to get permission from UserAPI Microservice
            List<String> userPermissions = getPermission();
            // Logic to check permission user has all the required permissions
            if (requiredPermissions.stream().allMatch(userPermissions::contains)) {
                log.info("User has required permission");
            } else {
                throw new PermissionDeniedException(messageSource.getMessage(MessageConstants.USER_PERMISSIONDENIED_RESOURCE, null, LocaleContextHolder.getLocale()));
            }
        }

    }

    /**
     * Check if the user has at least one permission from the required permissions
     * @param requiresPermission
     */
    @Before("@annotation(requiresPermission)")
    public void checkAnyPermission(RequiresAnyPermission requiresPermission) {
        System.out.println("Hello from RequiresAnyPermissions");
        List<String> requiredPermissions = Arrays.stream(requiresPermission.value()).map(Permission::toString).toList();
        requiredPermissions.forEach(System.out::println);

        if (!requiredPermissions.isEmpty()) {
            // Logic to get permission from UserAPI Microservice
            List<String> userPermissions = getPermission();
            // Check if the user has at least one permission from the required permissions
            // If it comese in a map of modules and permission than check for module and permission
            if (requiredPermissions.stream().anyMatch(userPermissions::contains)) {
                log.info("User has required permission");
            } else {
                throw new PermissionDeniedException(messageSource.getMessage(MessageConstants.USER_PERMISSIONDENIED_RESOURCE, null, LocaleContextHolder.getLocale()));
            }
        }
    }

    private Integer getUserId() {
        String email = JwtUtil.getEmailFromContext();
        HttpResponse userResponse = userAPIClient.getUserByEmail(email);
        UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
        return userData.getId();
    }

    private List<String> getPermission() {
        String[] userPermissions = {"WRITE", "DELETE", "EDIT"};
//        String[] userPermissions = {"READ"};
        return List.of(userPermissions);
    }

    private List<String> getPermission2() {
//        String[] userPermissions = {"READ"};
        return List.of("WRITE", "DELETE", "EDIT");
    }

}
