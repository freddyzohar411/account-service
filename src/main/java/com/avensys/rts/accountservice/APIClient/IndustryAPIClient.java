package com.avensys.rts.accountservice.APIClient;

import com.avensys.rts.accountservice.customresponse.HttpResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author Koh He Xiang
 * This class is an interface to interact with industry microservice
 */
@FeignClient(name = "industry-service", url = "http://localhost:8200")
public interface IndustryAPIClient {
    @GetMapping("/industries")
    HttpResponse getIndustryByName(@Valid @RequestParam String name);
}
