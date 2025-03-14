package com.ibanity.signatures.domain;

public class HostSignaturePart extends SignaturePart {

    public static final String NAME = "host";

    private final String host;

    HostSignaturePart(String host) {
        super(NAME);

        this.host = host;
    }

    @Override
    public String value() {
        return this.host;
    }

}
