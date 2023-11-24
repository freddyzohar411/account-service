package com.avensys.rts.accountservice.util;

import com.avensys.rts.accountservice.payloadnewresponse.user.ModuleResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.user.RoleResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.user.UserDetailsResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.user.UserGroupResponseDTO;

import java.util.*;

public class UserUtil {

	/**
	 * Map the user details to a map of module and permissions
	 * @param userDetailsResponse
	 * @return
	 */
	public static Map<String, Set<String>> mapUserDetailToUserPermissions(UserDetailsResponseDTO userDetailsResponse) {
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
	 *
	 * @param modulePermissions
	 * @param module
	 * @param requiredPermissions
	 * @return
	 */
	public static Boolean checkAllPermissionWithModule (Map<String, Set<String>> modulePermissions, String module, List<String> requiredPermissions) {
		if (modulePermissions.containsKey(module)) {
			Set<String> permissions = modulePermissions.get(module);
			if (permissions.containsAll(requiredPermissions)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get User usergroup in a list
	 */
	public static Set<Long> getUserGroupIds(UserDetailsResponseDTO userDetailsResponse) {
		Set<Long> userGroupIds = new HashSet<>();
		List<UserGroupResponseDTO> userGroups = userDetailsResponse.getUserGroup();
		if (userGroups == null || userGroups.isEmpty()) {
			return null;
		}
		for (UserGroupResponseDTO userGroup : userGroups) {
			userGroupIds.add(userGroup.getId());
		}
		return userGroupIds;
	}
}
