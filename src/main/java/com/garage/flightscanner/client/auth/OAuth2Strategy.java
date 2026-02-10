package com.garage.flightscanner.client.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Map;

public class OAuth2Strategy implements AuthStrategy {
    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;
    private final WebClient webClient;

    private String accessToken;
    private Instant tokenExpiry;

    public OAuth2Strategy(String tokenUrl, String clientId, String clientSecret, WebClient.Builder webClientBuilder) {
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.webClient = webClientBuilder.build();
    }

    @Override
    public void applyAuth(Map<String, String> headers, String method, String uri, byte[] body) {
        headers.put("Authorization", "Bearer " + getAccessToken());
    }

    private synchronized String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(tokenExpiry.minusSeconds(60))) {
            refreshToken();
        }
        return accessToken;
    }

    private void refreshToken() {
        String formBody = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret;

        TokenResponse response = webClient.post()
                .uri(tokenUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(formBody)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();

        if (response == null) {
            throw new RuntimeException("Failed to obtain access token");
        }

        this.accessToken = response.getAccessToken();
        this.tokenExpiry = Instant.now().plusSeconds(response.getExpiresIn());
    }

    @Data
    private static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private long expiresIn;
    }
}
