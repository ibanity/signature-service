package com.ibanity.signatures.domain;

public class AuthorizationSignaturePart extends SignaturePart {

    public static final String NAME = "authorization";

    private final String authorization;

    public AuthorizationSignaturePart(String authorization) {
        super(NAME);

        this.authorization = authorization;
    }

    @Override
    public String value() {
        return this.authorization;
    }

}
