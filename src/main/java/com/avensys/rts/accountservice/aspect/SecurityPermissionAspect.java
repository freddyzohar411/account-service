package com.avensys.rts.accountservice.aspect;

import com.avensys.rts.accountservice.APIClient.UserAPIClient;
import com.avensys.rts.accountservice.annotation.RequiresAllPermissions;
import com.avensys.rts.accountservice.annotation.RequiresAnyPermission;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.enums.Permission;
import com.avensys.rts.accountservice.exception.PermissionDeniedException;
import com.avensys.rts.accountservice.payloadresponse.UserResponseDTO;
import com.avensys.rts.accountservice.util.JwtUtil;
import com.avensys.rts.accountservice.util.MappingUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class SecurityPermissionAspect {

    @Autowired
    private UserAPIClient userAPIClient;

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
                System.out.println("User has permission");
            } else {
                System.out.println("User does not have permission");
                throw new PermissionDeniedException("User does not have permission");
            }
        }

    }

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
                System.out.println("User has permission");
            } else{
                System.out.println("User does not have permission");
                throw new PermissionDeniedException("User does not have permission");
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
