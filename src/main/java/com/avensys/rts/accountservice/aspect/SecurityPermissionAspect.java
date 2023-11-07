package com.avensys.rts.accountservice.aspect;

import com.avensys.rts.accountservice.APIClient.UserAPIClient;
import com.avensys.rts.accountservice.annotation.RequiresAllPermissions;
import com.avensys.rts.accountservice.annotation.RequiresAnyPermission;
import com.avensys.rts.accountservice.constant.MessageConstants;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.enums.Permission;
import com.avensys.rts.accountservice.exception.PermissionDeniedException;
import com.avensys.rts.accountservice.payloadnewresponse.user.ModuleResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.user.RoleResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.user.UserDetailsResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.user.UserGroupResponseDTO;
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

import java.util.*;

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

    @Before("@annotation(requiresPermission)")
    public void checkAllPermission(RequiresAllPermissions requiresPermission) {
        System.out.println("RequiresAllPermissions Aspect");
        List<String> requiredPermissions = Arrays.stream(requiresPermission.value()).map(Permission::toString).toList();
//        requiredPermissions.forEach(System.out::println);

        if (!requiredPermissions.isEmpty()) {
            // Logic to get permission from UserAPI Microservice
            Map<String, Set<String>> modulePermissions = mapUserDetailToUserPermissions(getUserDetails());
            if (modulePermissions != null) {
                requiredPermissions.forEach(modulePermission -> {
                    String[] modulePermissionArray = modulePermission.split(":");
                    if (!checkAPermissionWithModule(modulePermissions, modulePermissionArray[0], modulePermissionArray[1])) {
                        throw new PermissionDeniedException(messageSource.getMessage(MessageConstants.USER_PERMISSIONDENIED_RESOURCE, null, LocaleContextHolder.getLocale()));
                    }
                });
            } else {
                throw new PermissionDeniedException(messageSource.getMessage(MessageConstants.USER_PERMISSIONDENIED_RESOURCE, null, LocaleContextHolder.getLocale()));
            }
        }
    }

    /**
     * Check if the user has at least one permission from the required permissions
     *
     * @param requiresPermission
     */
    @Before("@annotation(requiresPermission)")
    public void checkAnyPermission(RequiresAnyPermission requiresPermission) {
        System.out.println("RequiresAnyPermissions Aspect");
        List<String> requiredPermissions = Arrays.stream(requiresPermission.value()).map(Permission::toString).toList();

        if (!requiredPermissions.isEmpty()) {
            // Logic to get permission from UserAPI Microservice
            Map<String, Set<String>> modulePermissions = mapUserDetailToUserPermissions(getUserDetails());
            // Check if one permission meet
            if (modulePermissions != null) {
                requiredPermissions.forEach(modulePermission -> {
                    String[] modulePermissionArray = modulePermission.split(":");
                    if (checkAPermissionWithModule(modulePermissions, modulePermissionArray[0], modulePermissionArray[1])) {
                        return;
                    }
                });
                throw new PermissionDeniedException(messageSource.getMessage(MessageConstants.USER_PERMISSIONDENIED_RESOURCE, null, LocaleContextHolder.getLocale()));
            } else {
                throw new PermissionDeniedException(messageSource.getMessage(MessageConstants.USER_PERMISSIONDENIED_RESOURCE, null, LocaleContextHolder.getLocale()));
            }
        }
    }


    private Boolean checkAPermissionWithModule (Map<String, Set<String>> modulePermissions, String module, String permission) {
        if (modulePermissions.containsKey(module)) {
            Set<String> permissions = modulePermissions.get(module);
            if (permissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    private Boolean checkAllPermissionWithModule (Map<String, Set<String>> modulePermissions, String module, List<String> requiredPermissions) {
        if (modulePermissions.containsKey(module)) {
            Set<String> permissions = modulePermissions.get(module);
            if (permissions.containsAll(requiredPermissions)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Map the user details response to a map of module and permissions
     * @param userDetailsResponse
     * @return
     */
    private Map<String, Set<String>> mapUserDetailToUserPermissions(UserDetailsResponseDTO userDetailsResponse) {
        Map<String, Set<String>> modulePermissions = new HashMap<>();
        // Check if Usergroup is null or empty
        List<UserGroupResponseDTO> userGroups = userDetailsResponse.getUserGroup();
        if (userGroups == null || userGroups.isEmpty()) {
            return null;
        }

        for (UserGroupResponseDTO userGroup : userGroups) {
            for (RoleResponseDTO role : userGroup.getRoles()) {
                for (ModuleResponseDTO module : role.getModules()) {
                    String moduleName = module.getModuleName();
                    Set<String> permissions = new HashSet<>(module.getPermissions());

                    // If the module already exists in the map, add to its permissions
                    modulePermissions.computeIfAbsent(moduleName, k -> new HashSet<>()).addAll(permissions);
                }
            }
        }
        return modulePermissions;
    }

    /**
     * Get the user id from the token
     * @return
     */
    private Integer getUserId() {
        String email = JwtUtil.getEmailFromContext();
        HttpResponse userResponse = userAPIClient.getUserByEmail(email);
        UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
        return userData.getId();
    }

    private UserDetailsResponseDTO getUserDetails() {
//        String email = JwtUtil.getEmailFromContext();
//        HttpResponse userResponse = userAPIClient.getUserDetailByEmail(email);
        HttpResponse userResponse = userAPIClient.getUserDetail();
        UserDetailsResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserDetailsResponseDTO.class);
        return userData;
    }

}
