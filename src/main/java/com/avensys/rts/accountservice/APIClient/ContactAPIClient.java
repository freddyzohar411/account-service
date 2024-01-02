package com.avensys.rts.accountservice.APIClient;

import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.interceptor.JwtTokenInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "contact-service", url = "${api.contact.url}", configuration = JwtTokenInterceptor.class)
public interface ContactAPIClient {

    @DeleteMapping("/contacts/entity/{entityType}/{entityId}")
    HttpResponse deleteContactsByEntityTypeAndEntityId(@PathVariable String entityType, @PathVariable Integer entityId);

}
