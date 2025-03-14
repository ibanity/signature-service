package com.ibanity.signatures.api;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibanity.signatures.services.HeaderService;
import com.ibanity.signatures.services.dto.Signature;
import com.ibanity.signatures.services.dto.SignatureRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/signatures")
public class SignaturesController {

    @Autowired
    private HeaderService headerService;

    @PostMapping
    public Signature createSignature(@Valid @RequestBody SignatureRequest signatureRequest) throws NoSuchAlgorithmException {
        return headerService.createSignatureHeader(signatureRequest);
    }

}
