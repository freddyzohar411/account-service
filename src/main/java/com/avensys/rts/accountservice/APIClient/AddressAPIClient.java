package com.avensys.rts.accountservice.APIClient;

import com.avensys.rts.accountservice.customresponse.HttpResponse;
import com.avensys.rts.accountservice.payloadrequest.AddressRequestDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * author Koh He Xiang
 * This class is an interface to interact with address microservice
 */
@FeignClient(name = "address-service", url = "http://localhost:8300")
public interface AddressAPIClient {
    @PostMapping("/addresses")
    HttpResponse createAddress(@Valid @RequestBody AddressRequestDTO addressRequest);

    @GetMapping("/addresses/{addressId}")
    HttpResponse getAddressById(@PathVariable int addressId);
}
