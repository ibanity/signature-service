package com.ibanity.signatures.domain;

import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ibanity.signatures.services.dto.SignatureRequest;
import com.ibanity.signatures.services.exception.EncryptionException;

public class SignatureParts implements Iterable<SignaturePart> {

    private final List<SignaturePart> signatureParts;
    
    private SignatureParts(List<SignaturePart> signatureParts) {
        this.signatureParts = signatureParts;
    }

    public static SignatureParts from(SignatureRequest signatureRequest, Clock clock) {
        return new SignatureParts(
            signatureParts(signatureRequest, clock)
        );
    }

    public Optional<SignaturePart> find(String name) {
        return signatureParts
                .stream()
                .filter(signaturePart -> signaturePart.name().equals(name))
                .findFirst();
    }

    private static List<SignaturePart> signatureParts(SignatureRequest signatureRequest, Clock clock) {
        try {
            return signaturePartsStream(signatureRequest, clock)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Error while creating signature parts", e);
        }
    }

    private static Stream<Optional<SignaturePart>> signaturePartsStream(SignatureRequest signatureRequest, Clock clock) throws NoSuchAlgorithmException {
        return Stream.of(
            SignaturePartFactory.requestTarget(signatureRequest),
            SignaturePartFactory.host(signatureRequest),
            SignaturePartFactory.digest(signatureRequest),
            SignaturePartFactory.created(signatureRequest, now(clock)),
            SignaturePartFactory.authorization(signatureRequest),
            SignaturePartFactory.ibanityIdempotencyKey(signatureRequest)
        );
    }

    private static long now(Clock clock) {
        return Instant.now(clock).getEpochSecond();
    }

    @Override
    public Iterator<SignaturePart> iterator() {
        return new SignaturePartsIterator();
    }

    private class SignaturePartsIterator implements Iterator<SignaturePart> {

        int index = 0;

        @Override
        public boolean hasNext() {
            return index < signatureParts.size();
        }

        @Override
        public SignaturePart next() {
            if (hasNext()) {
                return signatureParts.get(index++);
            } else {
                throw new NoSuchElementException();
            }
        }

    }

}
