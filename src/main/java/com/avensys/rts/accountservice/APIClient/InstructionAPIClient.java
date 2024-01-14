package com.avensys.rts.accountservice.APIClient;

import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.interceptor.JwtTokenInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "instruction-service", url = "${api.instruction.url}", configuration = JwtTokenInterceptor.class)
public interface InstructionAPIClient {

    @DeleteMapping(value = "/entity/{entityType}/{entityId}")
    HttpResponse deleteInstructionByEntityId(@PathVariable String entityType, @PathVariable int entityId);

    @GetMapping(value = "/entity/{entityType}/{entityId}")
    HttpResponse getInstructionByEntityId(@PathVariable String entityType, @PathVariable int entityId);
}
