package com.avensys.rts.accountservice.payloadnewresponse.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseListDTO {
	List<UserResponseDTO> users;
}
