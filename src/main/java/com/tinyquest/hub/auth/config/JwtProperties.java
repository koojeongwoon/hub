package com.tinyquest.hub.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String issuer = "tinyquest-hub";
    private Duration accessTokenTtl = Duration.ofMinutes(30);
    private Duration refreshTokenTtl = Duration.ofDays(7);
    private Duration allowedClockSkew = Duration.ofSeconds(30);
    private final List<Key> keys = new ArrayList<>();

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Duration getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public void setAccessTokenTtl(Duration accessTokenTtl) {
        this.accessTokenTtl = accessTokenTtl;
    }

    public Duration getRefreshTokenTtl() {
        return refreshTokenTtl;
    }

    public void setRefreshTokenTtl(Duration refreshTokenTtl) {
        this.refreshTokenTtl = refreshTokenTtl;
    }

    public Duration getAllowedClockSkew() {
        return allowedClockSkew;
    }

    public void setAllowedClockSkew(Duration allowedClockSkew) {
        this.allowedClockSkew = allowedClockSkew;
    }

    public List<Key> getKeys() {
        return Collections.unmodifiableList(keys);
    }

    public void setKeys(List<Key> keys) {
        this.keys.clear();
        if (keys != null) {
            this.keys.addAll(keys);
        }
    }

    public static class Key {
        private String kid;
        private String algorithm;
        private String secret;
        private String privateKey;
        private String publicKey;
        private boolean active;

        public String getKid() {
            return kid;
        }

        public void setKid(String kid) {
            this.kid = kid;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
