package com.ibanity.signatures.services;

import java.time.Clock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibanity.signatures.domain.CreatedSignaturePart;
import com.ibanity.signatures.domain.DigestSignaturePart;
import com.ibanity.signatures.domain.SignatureParts;
import com.ibanity.signatures.services.dto.Signature;
import com.ibanity.signatures.services.dto.SignatureRequest;
import com.ibanity.signatures.services.exception.SignaturePartsException;
import com.ibanity.signatures.services.http.SignatureService;

public class HeaderService {

    private static final String HEADER_FORMAT = "keyId=\"%s\", algorithm=\"%s\", created=%d, headers=\"%s\", signature=\"%s\"";
    private static final Logger LOG = LoggerFactory.getLogger(HeaderService.class);

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
        LOG.trace("# createSignatureHeader(SignatureRequest signatureRequest: {})", signatureRequest);

        SignatureParts signatureParts = SignatureParts.from(signatureRequest, clock);

        String signature = signatureService.sign(
            signingString(signatureParts)
        );

        return buildSignature(signatureParts, signature);
    }

    private String signingString(SignatureParts signatureParts) {
        LOG.trace("# signingString(SignatureParts signatureParts: {})", signatureParts);

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
        LOG.trace("# signedParts(SignatureParts signatureParts: {})", signatureParts);

        StringBuilder signedHeadersSb = new StringBuilder();

        signatureParts.forEach(header -> {
            signedHeadersSb.append(header.name().toLowerCase());
            signedHeadersSb.append(" ");
        });

        return signedHeadersSb.toString().trim();
    }

    private String digest(SignatureParts signatureParts) {
        LOG.trace("# digest(SignatureParts signatureParts: {})", signatureParts);

        return signatureParts
                .find(DigestSignaturePart.NAME)
                .orElseThrow(() -> new SignaturePartsException("No digest found!"))
                .value();
    }

    private String created(SignatureParts signatureParts) {
        LOG.trace("# created(SignatureParts signatureParts: {})", signatureParts);

        return signatureParts
                .find(CreatedSignaturePart.NAME)
                .orElseThrow(() -> new SignaturePartsException("No created found!"))
                .value();
    }

    private String createHeaderValue(String signedParts, String signature, long created) {
        LOG.trace("# createHeaderValue(String signedParts: {}, String signature: {}, long created: {})", signedParts, signature);

        return HEADER_FORMAT
                .trim()
                .formatted(keyId, algorithm, created, signedParts, signature);
    }

    private Signature buildSignature(SignatureParts signatureParts, String signature) {
        LOG.trace("# buildSignature(SignatureParts signatureParts: {}, String signature: {},)", signatureParts, signature);

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
