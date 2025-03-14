package com.ibanity.signatures.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ibanity.signatures.domain.HttpMethod;
import com.ibanity.signatures.services.dto.Signature;
import com.ibanity.signatures.services.dto.SignatureRequest;
import com.ibanity.signatures.services.http.SignatureService;

@ExtendWith(SpringExtension.class)
public class HeaderServiceTest {

    @Mock
    SignatureService signatureService;

    private static final String HOST = "api.ibanity.com";
    private static final String PATH = "/v1/accounts";
    private static final UUID IBANITY_IDEMPOTENCY_KEY = UUID.randomUUID();
    private static final String PAYLOAD = """
                {
                    "data": {
                        "type": "accounts",
                        "id": "c2c9e2e4-3f3"
                    }
                }
            """;
    private static final String KEY_ID = UUID.randomUUID().toString();
    private static final String ALGORITHM = "RSA-SHA256";
    private static final String EXPECTED_SIGNATURE = "(request-target):post/v1/accountshost:api.ibanity.comdigest:SHA-512=83AB6IBmG03hvK+JmAZBP+gFqICZSdae1DKtEMzQnXOC0nfqC1wAbM6xHvBGMqxSNW4OyUy6tH54U2R2ksoG/g==(created):31556889864403199ibanity-idempotency-key:" + IBANITY_IDEMPOTENCY_KEY;
    private static final String EXPECTED_SIGNATURE_HEADER = "keyId=\"" + KEY_ID + "\",algorithm=\"RSA-SHA256\",created=31556889864403199,headers=\"(request-target)hostdigest(created)ibanity-idempotency-key\",signature=\"" + EXPECTED_SIGNATURE + "\"";
    private Clock clock = Clock.fixed(Instant.MAX, ZoneId.systemDefault());
    private HeaderService headerService;

    @BeforeEach
    public void setup() {
        headerService = new HeaderService(signatureService, KEY_ID, ALGORITHM, clock);
    }

    @Test
    public void createSignatureHeader() throws Exception {
        when(signatureService.sign(Mockito.anyString())).thenAnswer(i -> i.getArguments()[0]);

        SignatureRequest signatureRequest = buildSignatureRequest();

        Signature signature = headerService.createSignatureHeader(signatureRequest);

        assertEquals(KEY_ID, signature.getKeyId());
        assertEquals(ALGORITHM, signature.getAlgorithm());
        assertEquals(31556889864403199L, signature.getCreated());
        assertEquals("(request-target) host digest (created) ibanity-idempotency-key", signature.getSignedHeaders());
        assertEquals("SHA-512=83AB6IBmG03hvK+JmAZBP+gFqICZSdae1DKtEMzQnXOC0nfqC1wAbM6xHvBGMqxSNW4OyUy6tH54U2R2ksoG/g==", signature.getDigest());
        assertEquals(EXPECTED_SIGNATURE, signature.getSignature().replaceAll("\\s", ""));
        assertEquals(EXPECTED_SIGNATURE_HEADER, signature.getSignatureHeader().replaceAll("\\s", ""));
    }

    private SignatureRequest buildSignatureRequest() {
        return SignatureRequest.builder().host(HOST).ibanityIdempotencyKey(IBANITY_IDEMPOTENCY_KEY)
                .method(HttpMethod.POST).path(PATH).payload(PAYLOAD).build();
    }

}
