package com.avensys.rts.accountservice.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avensys.rts.accountservice.APIClient.UserAPIClient;
import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.payloadnewresponse.user.ModuleResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.user.RoleResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.user.UserDetailsResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.user.UserGroupResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.user.UserResponseDTO;
import com.avensys.rts.accountservice.payloadnewresponse.user.UserResponseListDTO;

@Service
public class UserUtil {

	@Autowired
	private UserAPIClient userAPIClient;

	public Set<Long> getUserGroupIds(UserDetailsResponseDTO userDetailsResponse) {
		return mapUserDetailsToUserGroupIds(userDetailsResponse);
	}

	public Set<Long> getUserGroupIds() {
		UserDetailsResponseDTO userDetailsResponse = getUserDetails();
		return mapUserDetailsToUserGroupIds(userDetailsResponse);
	}

	public List<Long> getUsersIdUnderManager() {
		HttpResponse response = userAPIClient.getUsersUnderManager();
		return (List<Long>) response.getData();
	}

	public String getUserGroupIdsAsString() {
		Set<Long> userGroupIds = getUserGroupIds();
		StringJoiner joiner = new StringJoiner(",");
		for (Long value : userGroupIds) {
			joiner.add(value.toString());
		}
		return joiner.toString();
	}

	public String getUserGroupIdsAsString(UserDetailsResponseDTO userDetailsResponse) {
		Set<Long> userGroupIds = getUserGroupIds(userDetailsResponse);
		StringJoiner joiner = new StringJoiner(",");
		for (Long value : userGroupIds) {
			joiner.add(value.toString());
		}
		return joiner.toString();
	}

	/**
	 * Map the user details to a map of module and permissions
	 * 
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
	 * Check if the user has any of the permissions specified in the annotation
	 * 
	 * @param modulePermissions
	 * @param requiredPermissions
	 * @return
	 */
	public Boolean checkAPermissionWithModule(Map<String, Set<String>> modulePermissions, String module,
			String permission) {
		if (modulePermissions.containsKey(module)) {
			Set<String> permissions = modulePermissions.get(module);
			if (permissions.contains(permission)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param modulePermissions
	 * @param module
	 * @param requiredPermissions
	 * @return
	 */
	public Boolean checkAllPermissionWithModule(Map<String, Set<String>> modulePermissions, String module,
			List<String> requiredPermissions) {
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
	private Set<Long> mapUserDetailsToUserGroupIds(UserDetailsResponseDTO userDetailsResponse) {
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

	public UserDetailsResponseDTO getUserDetails() {
		HttpResponse userResponse = userAPIClient.getUserDetail();
		UserDetailsResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(),
				UserDetailsResponseDTO.class);
		return userData;
	}

	public String getUserNameEmail() {
		UserDetailsResponseDTO userDetails = getUserDetails();
		return userDetails.getFirstName() + " " + userDetails.getLastName() + " (" + userDetails.getEmail() + ")";
	}

	public List<String> getUserNameEmailUnderManager() {
		HttpResponse response = userAPIClient.getUsersUnderManagerEntity();
		UserResponseListDTO userDetails = MappingUtil.mapClientBodyToClass(response.getData(),
				UserResponseListDTO.class);
		List<String> userNames = new ArrayList<>();
		for (UserResponseDTO user : userDetails.getUsers()) {
			userNames.add(user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		}
		return userNames;
	}

	/**
	 * Get module permission
	 * 
	 * @return
	 */
	public Map<String, Set<String>> getModulePermissions() {
		Map<String, Set<String>> modulePermissions = mapUserDetailToUserPermissions(getUserDetails());
		return modulePermissions;
	}

	public Boolean checkIsAdmin() {
		Boolean flag = false;
		HttpResponse userResponse = userAPIClient.getUserDetail();
		UserDetailsResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(),
				UserDetailsResponseDTO.class);
		if (userData.getUserGroup() != null && userData.getUserGroup().size() > 0) {
			for (UserGroupResponseDTO grp : userData.getUserGroup()) {
				if (grp.getRoles() != null && grp.getRoles().size() > 0) {
					for (RoleResponseDTO role : grp.getRoles()) {
						if (role.getRoleName().toLowerCase().contains("admin")) {
							flag = true;
						}
					}
				}
			}
		}
		return flag;
	}

}
