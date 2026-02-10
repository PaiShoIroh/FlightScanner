package com.garage.flightscanner.client.exception;

/**
 * Exception for 4xx client errors from external APIs.
 */
public class ApiClientException extends ApiException {
    public ApiClientException(int statusCode, String responseBody) {
        super(statusCode, responseBody);
    }
}
