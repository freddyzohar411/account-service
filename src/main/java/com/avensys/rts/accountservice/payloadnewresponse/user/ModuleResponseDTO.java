package com.avensys.rts.accountservice.payloadnewresponse.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponseDTO {

	private Long id;
	private String moduleName;
	private List<String> permissions;
}
