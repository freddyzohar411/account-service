package com.avensys.rts.accountservice.service;

import com.avensys.rts.accountservice.payloadnewrequest.CommercialNewRequest;
import com.avensys.rts.accountservice.payloadnewresponse.CommercialNewResponseDTO;

public interface CommercialService {
    CommercialNewResponseDTO createCommercial(Integer id, CommercialNewRequest commercialNewRequest);

    CommercialNewResponseDTO getCommercial(Integer id);

    CommercialNewResponseDTO updateCommercial(Integer id, CommercialNewRequest commercialNewRequest);
}
