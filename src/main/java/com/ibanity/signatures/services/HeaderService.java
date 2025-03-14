package com.ibanity.signatures.services;

import java.time.Clock;

import com.ibanity.signatures.domain.CreatedSignaturePart;
import com.ibanity.signatures.domain.DigestSignaturePart;
import com.ibanity.signatures.domain.SignatureParts;
import com.ibanity.signatures.services.dto.Signature;
import com.ibanity.signatures.services.dto.SignatureRequest;
import com.ibanity.signatures.services.exception.SignaturePartsException;
import com.ibanity.signatures.services.http.SignatureService;

public class HeaderService {

    private static final String HEADER_FORMAT = "keyId=\"%s\", algorithm=\"%s\", created=%d, headers=\"%s\", signature=\"%s\"";
    
    private final SignatureService signatureService;
    private final String keyId;
    private final String algorithm;
    private final Clock clock;

    public HeaderService(SignatureService signatureService, String keyId, String algorithm, Clock clock) {
        this.signatureService = signatureService;
        this.keyId = keyId;
        this.algorithm = algorithm;
        this.clock = clock;
    }

    public Signature createSignatureHeader(SignatureRequest signatureRequest) {
        SignatureParts signatureParts = SignatureParts.from(signatureRequest, clock);

        String signature = signatureService.sign(
            signingString(signatureParts)
        );

        return buildSignature(signatureParts, signature);
    }

    private String signingString(SignatureParts signatureParts) {
        StringBuilder signingStringSb = new StringBuilder();

        signatureParts.forEach(header -> {
            signingStringSb.append(header.name().toLowerCase());
            signingStringSb.append(": ");
            signingStringSb.append(header.value());
            signingStringSb.append("\n");
        });

        return signingStringSb.toString().trim();
    }

    private String signedParts(SignatureParts signatureParts) {
        StringBuilder signedHeadersSb = new StringBuilder();

        signatureParts.forEach(header -> {
            signedHeadersSb.append(header.name().toLowerCase());
            signedHeadersSb.append(" ");
        });

        return signedHeadersSb.toString().trim();
    }

    private String digest(SignatureParts signatureParts) {
        return signatureParts
                .find(DigestSignaturePart.NAME)
                .orElseThrow(() -> new SignaturePartsException("No digest found!"))
                .value();
    }

    private String created(SignatureParts signatureParts) {
        return signatureParts
                .find(CreatedSignaturePart.NAME)
                .orElseThrow(() -> new SignaturePartsException("No created found!"))
                .value();
    }

    private String createHeaderValue(String signedParts, String signature, long created) {
        return HEADER_FORMAT
                .trim()
                .formatted(keyId, algorithm, created, signedParts, signature);
    }

    private Signature buildSignature(SignatureParts signatureParts, String signature) {
        String signedParts = signedParts(signatureParts);
        long created = Long.parseLong(created(signatureParts));
        String signatureHeader = createHeaderValue(signedParts, signature, created);

        return Signature.builder()
            .keyId(keyId)
            .algorithm(algorithm)
            .created(created)
            .signedHeaders(signedParts)
            .digest(digest(signatureParts))
            .signature(signature)
            .signatureHeader(signatureHeader)
            .build();
    }

}
