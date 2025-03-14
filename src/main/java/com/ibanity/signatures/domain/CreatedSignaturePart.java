package com.ibanity.signatures.domain;

public class CreatedSignaturePart extends SignaturePart {

    public static final String NAME = "(created)";
    
    private final long timestamp;

    CreatedSignaturePart(long timestamp) {
        super(NAME);
        
        this.timestamp = timestamp;
    }

    @Override
    public String value() {
        return Long.toString(timestamp);
    }

}
