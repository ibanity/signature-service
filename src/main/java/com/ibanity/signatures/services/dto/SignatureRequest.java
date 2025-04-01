package com.ibanity.signatures.services.dto;

import static com.ibanity.signatures.domain.HttpMethod.DELETE;
import static com.ibanity.signatures.domain.HttpMethod.GET;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNullElse;

import java.util.UUID;

import com.ibanity.signatures.domain.HttpMethod;
import com.ibanity.signatures.services.exception.PayloadException;

public class SignatureRequest {

    private String host;
    private String path;
    private HttpMethod method;
    private String payload;
    private String authorization;
    private UUID ibanityIdempotencyKey;

    private SignatureRequest(
        String host,
        String path,
        HttpMethod method,
        String payload,
        String authorization,
        UUID ibanityIdempotencyKey) {
        this.host = host;
        this.path = path;
        this.method = method;
        this.ibanityIdempotencyKey = ibanityIdempotencyKey;
        this.authorization = authorization;
        this.payload = createPayload(method, payload);
    }

    private String createPayload(HttpMethod method, String payload) {
        if (payloadRequired(method) && isNull(payload)) {
            throw new PayloadException();
        }
        
        return requireNonNullElse(payload, "");
    }

    private boolean payloadRequired(HttpMethod method) {
        return method != GET && method != DELETE;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPayload() {
        return payload;
    }

    public String getAuthorization() {
        return authorization;
    }

    public UUID getIbanityIdempotencyKey() {
        return ibanityIdempotencyKey;
    }

    @Override
    public String toString() {
        return """
            host=%s, path=%s, method=%s, payload=%s, authorization=%s, ibanityIdempotencyKey=%s
        """
        .trim()
        .formatted(host, path, method, payload, authorization, ibanityIdempotencyKey);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String host;
        private String path;
        private HttpMethod method;
        private String payload;
        private String authorization;
        private UUID ibanityIdempotencyKey;

        public Builder() {}

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder authorization(String authorization) {
            this.authorization = authorization;
            return this;
        }

        public Builder ibanityIdempotencyKey(UUID ibanityIdempotencyKey) {
            this.ibanityIdempotencyKey = ibanityIdempotencyKey;
            return this;
        }

        public SignatureRequest build() {
            return new SignatureRequest(host, path, method, payload, authorization, ibanityIdempotencyKey);
        }
    }

}
