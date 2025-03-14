package com.ibanity.signatures.services.dto;

public class Signature {

    private final String keyId;
    private final String algorithm;
    private final long created;
    private final String signedHeaders;
    private final String digest;
    private final String signature;
    private final String signatureHeader;

    private Signature(String keyId, String algorithm, long created, String signedHeaders, String digest,
            String signature, String signatureHeader) {
        this.keyId = keyId;
        this.algorithm = algorithm;
        this.created = created;
        this.signedHeaders = signedHeaders;
        this.digest = digest;
        this.signature = signature;
        this.signatureHeader = signatureHeader;
    }

    public String getKeyId() {
        return keyId;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public long getCreated() {
        return created;
    }
    
    public String getSignedHeaders() {
        return signedHeaders;
    }
    
    public String getDigest() {
        return digest;
    }
    
    public String getSignature() {
        return signature;
    }
    
    public String getSignatureHeader() {
        return signatureHeader;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String keyId;
        private String algorithm;
        private long created;
        private String signedHeaders;
        private String digest;
        private String signature;
        private String signatureHeader;

        public Builder() {}

        public Builder keyId(String keyId) {
            this.keyId = keyId;
            return this;
        }

        public Builder algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder created(long created) {
            this.created = created;
            return this;
        }

        public Builder signedHeaders(String signedHeaders) {
            this.signedHeaders = signedHeaders;
            return this;
        }

        public Builder digest(String digest) {
            this.digest = digest;
            return this;
        }

        public Builder signature(String signature) {
            this.signature = signature;
            return this;
        }

        public Builder signatureHeader(String signatureHeader) {
            this.signatureHeader = signatureHeader;
            return this;
        }

        public Signature build() {
            return new Signature(keyId, algorithm, created, signedHeaders, digest, signature, signatureHeader);
        }
    }
    
}
