package com.avensys.rts.accountservice.payloadrequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Koh He Xiang
 * This is the DTO class for the document delete request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDeleteRequestDTO {
    private String type;
    private int entityId;
}
