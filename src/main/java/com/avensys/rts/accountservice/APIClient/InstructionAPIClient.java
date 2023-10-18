package com.avensys.rts.accountservice.APIClient;

import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.interceptor.JwtTokenInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "instruction-service", url = "http://localhost:8800", configuration = JwtTokenInterceptor.class)
public interface InstructionAPIClient {

    @DeleteMapping(value = "/instructions/entity/{entityType}/{entityId}")
    HttpResponse deleteInstructionByEntityId(@PathVariable String entityType, @PathVariable int entityId);
}
