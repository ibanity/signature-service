package com.ibanity.signatures.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.ibanity.signatures.services.exception.EncryptionException;

public class DigestSignaturePart extends SignaturePart {

    public static final String NAME = "digest";

    private static final String HASH_ALGORITHM = "SHA-512";

    private final String encodedPayload;

    DigestSignaturePart(String payload) {
        super(NAME);

        byte[] hashedPayload = hash(payload);
        this.encodedPayload = encode(hashedPayload);
    }

    private byte[] hash(String payload) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            return md.digest(payload.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Could not find algorithm: " + HASH_ALGORITHM, e);
        }
    }

    private String encode(byte[] payload) {
        return Base64.getEncoder().encodeToString(payload);
    }

    @Override
    public String value() {
        return "SHA-512=" + this.encodedPayload;
    }

}
