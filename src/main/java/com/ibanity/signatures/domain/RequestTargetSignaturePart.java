package com.ibanity.signatures.domain;

public class RequestTargetSignaturePart extends SignaturePart {

    public  static final String NAME = "(request-target)";
    
    private final String path;
    private final HttpMethod method;

    RequestTargetSignaturePart(String path, HttpMethod method) {
        super(NAME);

        this.path = path;
        this.method = method;
    }

    @Override
    public String value() {
        return this.method.name().toLowerCase() + " " + path;
    }

}
