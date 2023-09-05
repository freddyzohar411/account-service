package com.avensys.rts.accountservice.payloadresponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Koh He Xiang
 * This is the DTO class for the Commercial response
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommercialResponseDTO {
    private Double markUp;
    private Double msp ;
}
