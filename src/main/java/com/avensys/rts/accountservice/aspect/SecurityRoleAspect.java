package com.avensys.rts.accountservice.aspect;

import com.avensys.rts.accountservice.APIClient.UserAPIClient;
import com.avensys.rts.accountservice.annotation.RequiresAllPermissions;
import com.avensys.rts.accountservice.annotation.RequiresAllRoles;
import com.avensys.rts.accountservice.annotation.RequiresAnyPermission;
import com.avensys.rts.accountservice.annotation.RequiresAnyRole;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.enums.Permission;
import com.avensys.rts.accountservice.enums.Role;
import com.avensys.rts.accountservice.exception.PermissionDeniedException;
import com.avensys.rts.accountservice.payloadresponse.UserResponseDTO;
import com.avensys.rts.accountservice.util.JwtUtil;
import com.avensys.rts.accountservice.util.MappingUtil;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class SecurityRoleAspect {

    @Autowired
    private UserAPIClient userAPIClient;

    @Autowired
    private MessageSource messageSource;

    @Before("@annotation(requiresRole)")
    public void checkAllRoles(RequiresAllRoles requiresRole) {
        System.out.println("Hello from RequiresAllRoles");
        List<String> requiredRoles = Arrays.stream(requiresRole.value()).map(Role::toString).toList();
        requiredRoles.forEach(System.out::println);


        if (!requiredRoles.isEmpty()) {
            // Logic to get roles from UserAPI Microservice
            List<String> userRoles = getRoles();
            // Logic to check user has all the required roles
            if (requiredRoles.stream().allMatch(userRoles::contains)) {
                System.out.println("User has required roles");
            } else {
                System.out.println("User does not have the required roles");
                throw new PermissionDeniedException("User does not have required roles");
            }
        }

    }

    @Before("@annotation(requiresRole)")
    public void checkAnyRole(RequiresAnyRole requiresRole) {
        System.out.println("Hello from RequiresAnyRoles");
        List<String> requiredRoles = Arrays.stream(requiresRole.value()).map(Role::toString).toList();
        requiredRoles.forEach(System.out::println);

        if (!requiredRoles.isEmpty()) {
            // Logic to get roles from UserAPI Microservice
            List<String> userRoles = getRoles();
            // Check if the user has at least one permission from the required permissions
            if (requiredRoles.stream().anyMatch(userRoles::contains)) {
                System.out.println("User has roles");
            } else {
                System.out.println("User does not have required roles");
                throw new PermissionDeniedException("User does not have required roles");
            }
        }
    }

    private Integer getUserId() {
        String email = JwtUtil.getEmailFromContext();
        HttpResponse userResponse = userAPIClient.getUserByEmail(email);
        UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
        return userData.getId();
    }

    private List<String> getRoles() {
        String[] userRoles = {"ADMIN", "USER", "SUPERADMIN"};
//        String[] userPermissions = {"READ"};
        return List.of(userRoles);
    }
}
