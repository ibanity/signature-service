package com.ibanity.signatures.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.ibanity.signatures.services.dto.SignatureRequest;

public class SignaturePartsTest {

    private static final String HOST = "api.ibanity.com";
    private static final String PATH = "/v1/accounts";
    private static final UUID IBANITY_IDEMPOTENCY_KEY = UUID.randomUUID();
    private static final String AUTHORIZATION = "Bearer 123";
    private static final String PAYLOAD = """
        {
            "data": {
                "type": "accounts",
                "id": "c2c9e2e4-3f3"
            }    
        } 
    """;

    @Test
    void from_shouldCreateAllSignatureParts_withPayload() throws Exception {
        SignatureRequest signatureRequest = SignatureRequest
                                                .builder()
                                                .host(HOST)
                                                .ibanityIdempotencyKey(IBANITY_IDEMPOTENCY_KEY)
                                                .method(HttpMethod.POST)
                                                .path(PATH)
                                                .payload(PAYLOAD)
                                                .authorization(AUTHORIZATION)
                                                .build();

        Clock fixedClock = Clock.systemDefaultZone();

        SignatureParts signatureParts = SignatureParts.from(signatureRequest, fixedClock);

        List<String> expectedParts = List.of("digest", "(request-target)", "ibanity-idempotency-key", "host", "authorization", "(created)");
        AtomicInteger count = new AtomicInteger(0);

        signatureParts.forEach(part -> {
            assertTrue(expectedParts.contains(part.name()));
            assertNotNull(part.value());
            
            count.incrementAndGet();
        });

        assertEquals(6, count.get());
    }

    @Test
    void from_shouldCreateAllSignatureParts_withEmptyPayload() throws Exception {
        SignatureRequest signatureRequest = SignatureRequest
                                                .builder()
                                                .host(HOST)
                                                .ibanityIdempotencyKey(IBANITY_IDEMPOTENCY_KEY)
                                                .method(HttpMethod.GET)
                                                .path(PATH)
                                                .payload("")
                                                .authorization(AUTHORIZATION)
                                                .build();

        Clock fixedClock = Clock.systemDefaultZone();

        SignatureParts signatureParts = SignatureParts.from(signatureRequest, fixedClock);

        List<String> expectedParts = List.of("digest", "(request-target)", "ibanity-idempotency-key", "host", "authorization", "(created)");
        AtomicInteger count = new AtomicInteger(0);

        signatureParts.forEach(part -> {
            assertTrue(expectedParts.contains(part.name()));
            assertNotNull(part.value());
            
            count.incrementAndGet();
        });

        assertEquals(6, count.get());
    }

    @Test
    void from_withoutIbanityIdempotencyKey() throws Exception {
        SignatureRequest signatureRequest = SignatureRequest
                                                .builder()
                                                .host(HOST)
                                                .method(HttpMethod.POST)
                                                .path(PATH)
                                                .payload(PAYLOAD)
                                                .authorization(AUTHORIZATION)
                                                .build();

        Clock fixedClock = Clock.systemDefaultZone();

        SignatureParts signatureParts = SignatureParts.from(signatureRequest, fixedClock);

        List<String> expectedParts = List.of("digest", "(request-target)", "host", "authorization", "(created)");
        AtomicInteger count = new AtomicInteger(0);

        signatureParts.forEach(part -> {
            assertTrue(expectedParts.contains(part.name()));
            assertNotNull(part.value());
            
            count.incrementAndGet();
        });

        assertEquals(5, count.get());
    }

    @Test
    void from_withoutAuthorization() throws Exception {
        SignatureRequest signatureRequest = SignatureRequest
                                                .builder()
                                                .host(HOST)
                                                .method(HttpMethod.POST)
                                                .path(PATH)
                                                .payload(PAYLOAD)
                                                .ibanityIdempotencyKey(IBANITY_IDEMPOTENCY_KEY)
                                                .build();

        Clock fixedClock = Clock.systemDefaultZone();

        SignatureParts signatureParts = SignatureParts.from(signatureRequest, fixedClock);

        List<String> expectedParts = List.of("digest", "(request-target)", "ibanity-idempotency-key", "host", "(created)");
        AtomicInteger count = new AtomicInteger(0);

        signatureParts.forEach(part -> {
            assertTrue(expectedParts.contains(part.name()));
            assertNotNull(part.value());
            
            count.incrementAndGet();
        });

        assertEquals(5, count.get());
    }

    @Test
    void from_withoutIbanityIdempotencyKeyAndAuthorization() throws Exception {
        SignatureRequest signatureRequest = SignatureRequest
                                                .builder()
                                                .host(HOST)
                                                .method(HttpMethod.POST)
                                                .path(PATH)
                                                .payload(PAYLOAD)
                                                .build();

        Clock fixedClock = Clock.systemDefaultZone();

        SignatureParts signatureParts = SignatureParts.from(signatureRequest, fixedClock);

        List<String> expectedParts = List.of("digest", "(request-target)", "host", "(created)");
        AtomicInteger count = new AtomicInteger(0);

        signatureParts.forEach(part -> {
            assertTrue(expectedParts.contains(part.name()));
            assertNotNull(part.value());
            
            count.incrementAndGet();
        });

        assertEquals(4, count.get());
    }

}
