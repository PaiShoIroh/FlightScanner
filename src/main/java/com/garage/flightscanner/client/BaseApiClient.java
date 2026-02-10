package com.garage.flightscanner.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garage.flightscanner.client.auth.AuthStrategy;
import com.garage.flightscanner.client.exception.ApiClientException;
import com.garage.flightscanner.client.exception.ApiServerException;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BaseApiClient {
    protected final WebClient webClient;
    protected final ObjectMapper objectMapper;
    protected final AuthStrategy authStrategy;

    public BaseApiClient(WebClient.Builder webClientBuilder, String baseUrl, AuthStrategy authStrategy) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.objectMapper = new ObjectMapper();
        this.authStrategy = authStrategy;
    }

    public <R> R get(String uri, Class<R> responseType) {
        return get(uri, responseType, headers -> {});
    }

    public <R> R get(String uri, Class<R> responseType, Consumer<Map<String, String>> headerCustomizer) {
        return webClient.get()
                .uri(uri)
                .headers(httpHeaders -> {
                    Map<String, String> customHeaders = new HashMap<>();
                    if (authStrategy != null) {
                        authStrategy.applyAuth(customHeaders, "GET", uri, null);
                    }
                    headerCustomizer.accept(customHeaders);
                    customHeaders.forEach(httpHeaders::set);
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                    response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new ApiClientException(response.statusCode().value(), body)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                    response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new ApiServerException(response.statusCode().value(), body)))
                )
                .bodyToMono(responseType)
                .block();
    }

    public <T, R> R post(String uri, T body, Class<R> responseType) {
        return post(uri, body, responseType, headers -> {});
    }

    public <T, R> R post(String uri, T body, Class<R> responseType, Consumer<Map<String, String>> headerCustomizer) {
        byte[] bodyBytes = serializeToBytes(body);

        return webClient.post()
                .uri(uri)
                .headers(httpHeaders -> {
                    Map<String, String> customHeaders = new HashMap<>();
                    customHeaders.put("Content-Type", "application/json");
                    if (authStrategy != null) {
                        authStrategy.applyAuth(customHeaders, "POST", uri, bodyBytes);
                    }
                    headerCustomizer.accept(customHeaders);
                    customHeaders.forEach(httpHeaders::set);
                })
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                    response.bodyToMono(String.class)
                        .flatMap(b -> Mono.error(new ApiClientException(response.statusCode().value(), b)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                    response.bodyToMono(String.class)
                        .flatMap(b -> Mono.error(new ApiServerException(response.statusCode().value(), b)))
                )
                .bodyToMono(responseType)
                .block();
    }

    public <R> R postForm(String uri, Map<String, String> formData, Class<R> responseType) {
        return webClient.post()
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(toFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                    response.bodyToMono(String.class)
                        .flatMap(b -> Mono.error(new ApiClientException(response.statusCode().value(), b)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                    response.bodyToMono(String.class)
                        .flatMap(b -> Mono.error(new ApiServerException(response.statusCode().value(), b)))
                )
                .bodyToMono(responseType)
                .block();
    }

    protected byte[] serializeToBytes(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize payload", e);
        }
    }

    private String toFormData(Map<String, String> data) {
        return data.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }
}
