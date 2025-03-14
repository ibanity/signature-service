package com.ibanity.signatures.services.exception;

public class PayloadException extends RuntimeException {

    public PayloadException() {
        super("Payload is required for POST and PUT requests.");
    }

}
