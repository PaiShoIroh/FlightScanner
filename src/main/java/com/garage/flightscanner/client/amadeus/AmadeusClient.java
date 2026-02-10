package com.garage.flightscanner.client.amadeus;

import com.garage.flightscanner.client.BaseApiClient;
import com.garage.flightscanner.client.auth.OAuth2Strategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AmadeusClient extends BaseApiClient {

    private static final String TEST_BASE_URL = "https://test.api.amadeus.com";
    private static final String PROD_BASE_URL = "https://api.amadeus.com";
    private static final String TOKEN_PATH = "/v1/security/oauth2/token";

    public AmadeusClient(
            WebClient.Builder webClientBuilder,
            @Value("${amadeus.client-id}") String clientId,
            @Value("${amadeus.client-secret}") String clientSecret,
            @Value("${amadeus.environment:test}") String environment) {

        super(
            webClientBuilder,
            resolveBaseUrl(environment),
            new OAuth2Strategy(
                resolveBaseUrl(environment) + TOKEN_PATH,
                clientId,
                clientSecret,
                webClientBuilder
            )
        );
    }

    private static String resolveBaseUrl(String environment) {
        return "production".equalsIgnoreCase(environment) ? PROD_BASE_URL : TEST_BASE_URL;
    }

    public FlightOffersResponse searchFlights(FlightSearchRequest request) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromPath("/v2/shopping/flight-offers")
                .queryParam("originLocationCode", request.getOrigin())
                .queryParam("destinationLocationCode", request.getDestination())
                .queryParam("departureDate", request.getDepartureDate())
                .queryParam("adults", request.getAdults());

        if (request.getReturnDate() != null && !request.getReturnDate().isBlank()) {
            uriBuilder.queryParam("returnDate", request.getReturnDate());
        }
        if (request.getMaxResults() != null) {
            uriBuilder.queryParam("max", request.getMaxResults());
        }
        if (request.isNonStop()) {
            uriBuilder.queryParam("nonStop", true);
        }

        return get(uriBuilder.toUriString(), FlightOffersResponse.class);
    }
}
