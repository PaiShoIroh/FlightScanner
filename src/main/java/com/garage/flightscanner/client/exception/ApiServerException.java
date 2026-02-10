package com.garage.flightscanner.client.exception;

/**
 * Exception for 5xx server errors from external APIs.
 */
public class ApiServerException extends ApiException {
    public ApiServerException(int statusCode, String responseBody) {
        super(statusCode, responseBody);
    }
}
