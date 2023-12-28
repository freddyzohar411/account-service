package com.avensys.rts.accountservice.APIClient;

import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.interceptor.JwtTokenInterceptor;
import com.avensys.rts.accountservice.payloadnewrequest.CommercialRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/**
 * author Koh He Xiang
 * This class is an interface to interact with document microservice
 */
@Configuration
@FeignClient(name = "document-service", url = "${api.document.url}", configuration = JwtTokenInterceptor.class)
public interface DocumentAPIClient {
    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    HttpResponse createDocument(@ModelAttribute CommercialRequest.DocumentRequestDTO documentRequest);

    @PutMapping(value = "/documents" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    HttpResponse updateDocument(@ModelAttribute CommercialRequest.DocumentRequestDTO documentRequest);

    @DeleteMapping("/documents")
    HttpResponse deleteDocumentByEntityIdAndType(@RequestBody CommercialRequest.DocumentDeleteRequestDTO documentDeleteRequestDTO);

    @GetMapping("/documents")
    HttpResponse getDocumentByEntityTypeAndId(@RequestParam String entityType, @RequestParam int entityId);

    @DeleteMapping("/documents/entity/{entityType}/{entityId}")
    HttpResponse deleteDocumentsByEntityTypeAndEntityId(@PathVariable String entityType, @PathVariable Integer entityId);
}
