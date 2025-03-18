package com.ibanity.signatures.services.http;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibanity.signatures.services.exception.EncryptionException;

public class SignatureService {

    private static final String SIGNATURE_ALGORITHM = "RSASSA-PSS";
    private static final PSSParameterSpec PARAMETER_SPEC = new PSSParameterSpec("SHA-256", "MGF1",
            MGF1ParameterSpec.SHA256, 32, 1);
    private static final Logger LOG = LoggerFactory.getLogger(SignatureService.class);
    
    private final PrivateKey privateKey;

    public SignatureService(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String sign(String toSign) {
        LOG.trace("# sign(toSign: String {})", toSign);

        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);

            signature.setParameter(PARAMETER_SPEC);
            signature.initSign(privateKey);
            signature.update(toSign.getBytes(UTF_8));

            byte[] signedData = signature.sign();

            return new String(Base64.getEncoder().encode(signedData), UTF_8);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | SignatureException
                | InvalidKeyException e) {
            throw new EncryptionException("Error while trying to generate the signature", e);
        }

    }
}
