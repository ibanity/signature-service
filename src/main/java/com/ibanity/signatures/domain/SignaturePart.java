package com.ibanity.signatures.domain;

public abstract class SignaturePart {

    protected final String name;

    protected SignaturePart(String name) {
        this.name = name;
    }

    public abstract String value();
    
    public String name() {
        return this.name;
    }

    @Override
    public String toString() {
        return "name=" + name + ", value=" + value();
    }

}
