package com.ibanity.signatures.configuration;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Clock;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ibanity.signatures.services.HeaderService;
import com.ibanity.signatures.services.http.SignatureService;

@Configuration
public class AppConfiguration {

    @Value("${ibanity.key-id}")
    private String keyId;

    @Value("${ibanity.private-key.path}")
    private String privateKeyPath;

    private static final String SIGNATURE_ALGORITHM = "hs2019";

    @Bean
    public Clock headerServiceClock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public PrivateKey privateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String formattedKey = formatKey(
            Files.readAllBytes(Paths.get(privateKeyPath))
        );
        byte[] decoded = Base64.getDecoder().decode(formattedKey);
                                
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

        return keyFactory.generatePrivate(keySpec);
    }

    @Bean
    public SignatureService signatureService(PrivateKey privateKey) {
        return new SignatureService(privateKey);
    }

    @Bean
    public HeaderService headerService(SignatureService signatureService, Clock headerServiceClock) {
        return new HeaderService(signatureService, keyId, SIGNATURE_ALGORITHM, headerServiceClock);
    }

    private String formatKey(byte[] key) {
        return new String(key, UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PRIVATE KEY-----", "");
    }
}
