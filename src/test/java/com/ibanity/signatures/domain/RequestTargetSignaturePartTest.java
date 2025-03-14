package com.ibanity.signatures.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RequestTargetSignaturePartTest {

    @Test
    void value_shouldReturnMethodAndPath() {
        RequestTargetSignaturePart requestTarget = new RequestTargetSignaturePart("/accounts", HttpMethod.GET);
        
        String actual = requestTarget.value();
        
        assertEquals("get /accounts", actual);
    }
}
