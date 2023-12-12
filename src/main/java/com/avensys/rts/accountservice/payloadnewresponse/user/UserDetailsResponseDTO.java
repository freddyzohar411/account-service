package com.avensys.rts.accountservice.payloadnewresponse.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailsResponseDTO {

	private Long id;
	private String keycloackId;
	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private String mobile;
	private String employeeId;
	private Boolean locked;
	private Boolean enabled;
	private List<UserGroupResponseDTO> userGroup;
}
