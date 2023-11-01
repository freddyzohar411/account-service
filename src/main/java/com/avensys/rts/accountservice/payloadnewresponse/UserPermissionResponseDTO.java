package com.avensys.rts.accountservice.payloadnewresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPermissionResponseDTO {
    private Map<String,List<String>> permissions;
}
