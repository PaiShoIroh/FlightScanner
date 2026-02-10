package com.garage.flightscanner.client.auth;

import java.util.Map;

public interface AuthStrategy {
    void applyAuth(Map<String, String> headers, String method, String uri, byte[] body);
}
