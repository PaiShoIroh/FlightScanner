package com.garage.flightscanner.client.exception;

import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {
    private final int statusCode;
    private final String responseBody;

    protected ApiException(int statusCode, String responseBody) {
        super("API error " + statusCode + ": " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}
