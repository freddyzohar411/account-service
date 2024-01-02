package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.payloadnewrequest.CommercialRequest;
import com.avensys.rts.accountservice.payloadnewresponse.CommercialResponseDTO;

public interface CommercialService {
    CommercialResponseDTO createCommercial(Integer id, CommercialRequest commercialRequest);

    CommercialResponseDTO getCommercial(Integer id);

    CommercialResponseDTO updateCommercial(Integer id, CommercialRequest commercialRequest);
}
