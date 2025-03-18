package com.ibanity.signatures.api;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibanity.signatures.domain.HttpMethod;
import com.ibanity.signatures.services.HeaderService;
import com.ibanity.signatures.services.dto.Signature;
import com.ibanity.signatures.services.dto.SignatureRequest;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/signatures")
public class SignaturesController {

    @Autowired
    private HeaderService headerService;

    @PostMapping(
        consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE}
    )
    public Signature createSignature(
        @RequestParam("host") @NotBlank String host,
        @RequestParam("path") @NotBlank String path,
        @RequestParam("method") HttpMethod method,
        @RequestParam("ibanityIdempotencyKey") Optional<UUID> ibanityIdempotencyKey,
        @RequestParam("authorization") Optional<String> authorization,
        @RequestBody Optional<String> payload) throws NoSuchAlgorithmException {

        SignatureRequest.Builder signatureRequestBuilder = 
            SignatureRequest
                .builder()
                .host(host)
                .path(path)
                .method(method);

        ibanityIdempotencyKey.ifPresent(signatureRequestBuilder::ibanityIdempotencyKey);
        authorization.ifPresent(signatureRequestBuilder::authorization);
        payload.ifPresent(signatureRequestBuilder::payload);  
              
        return headerService.createSignatureHeader(signatureRequestBuilder.build());
    }

}
