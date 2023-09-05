package com.avensys.rts.accountservice.payloadresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Koh He Xiang
 * This is the DTO class for the document response
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponseDTO {
    private Integer id;
    private String type;
    private String title;
    private String documentName;
    private String description;
    private Integer entityId;
    private String entityType;
}
