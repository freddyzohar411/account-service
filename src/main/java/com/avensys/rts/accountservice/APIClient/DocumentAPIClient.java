package com.avensys.rts.accountservice.APIClient;

import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.payloadrequest.DocumentDeleteRequestDTO;
import com.avensys.rts.accountservice.payloadrequest.DocumentRequestDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * author Koh He Xiang
 * This class is an interface to interact with document microservice
 */
@Configuration
@FeignClient(name = "document-service", url = "http://localhost:8500")
public interface DocumentAPIClient {
    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    HttpResponse createDocument(@ModelAttribute DocumentRequestDTO documentRequest);

    @PutMapping(value = "/documents" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    HttpResponse updateDocument(@ModelAttribute DocumentRequestDTO documentRequest);

    @DeleteMapping("/documents")
    HttpResponse deleteDocumentByEntityIdAndType(@RequestBody DocumentDeleteRequestDTO documentDeleteRequestDTO);

}
