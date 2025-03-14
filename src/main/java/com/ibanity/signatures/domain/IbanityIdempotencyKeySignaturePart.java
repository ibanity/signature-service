package com.ibanity.signatures.domain;

import java.util.UUID;

public class IbanityIdempotencyKeySignaturePart extends SignaturePart {

    public  static final String NAME = "ibanity-idempotency-key";
    
    private final String key;

    IbanityIdempotencyKeySignaturePart(UUID key) {
        super(NAME);

        this.key = key.toString();
    }

    @Override
    public String value() {
        return this.key;
    }

}
