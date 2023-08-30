package com.avensys.rts.accountservice.payloadrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponseDTO {
    private int id;
    private String type;
    private String title;
    private String description;
    private int entityId;
}
