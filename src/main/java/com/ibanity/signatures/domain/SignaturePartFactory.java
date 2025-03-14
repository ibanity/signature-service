package com.ibanity.signatures.domain;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import com.ibanity.signatures.services.dto.SignatureRequest;

public class SignaturePartFactory {

    public static Optional<SignaturePart> digest(SignatureRequest signatureRequest) throws NoSuchAlgorithmException {
        return Optional.of(
            new DigestSignaturePart(
                signatureRequest.getPayload()
            )
        );
    }

    public static Optional<SignaturePart> requestTarget(SignatureRequest signatureRequest) {
        return Optional.of(
            new RequestTargetSignaturePart(
                signatureRequest.getPath(),
                signatureRequest.getMethod()
            )
        );
    }

    public static Optional<SignaturePart> ibanityIdempotencyKey(SignatureRequest signatureRequest) {
        return Optional
                .ofNullable(signatureRequest.getIbanityIdempotencyKey())
                .map(IbanityIdempotencyKeySignaturePart::new);
    }

    public static Optional<SignaturePart> host(SignatureRequest signatureRequest) {
        return Optional.of(
            new HostSignaturePart(
                signatureRequest.getHost()
            )
        );
    }

    public static Optional<SignaturePart> created(SignatureRequest signatureRequest, long timestamp) {
        return Optional.of(
            new CreatedSignaturePart(timestamp)
        );
    }

    public static Optional<SignaturePart> authorization(SignatureRequest signatureRequest) {
        return Optional
                .ofNullable(signatureRequest.getAuthorization())
                .map(AuthorizationSignaturePart::new);
    }

}
