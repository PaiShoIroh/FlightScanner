package com.garage.flightscanner.client.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class HmacStrategy implements AuthStrategy {
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final String secretKey;

    public HmacStrategy(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public void applyAuth(Map<String, String> headers, String method, String uri, byte[] body) {
        headers.put("X-Timestamp", String.valueOf(System.currentTimeMillis()));
        String signature = generateSignature(method, uri, body, headers);
        headers.put("X-Signature", signature);
    }

    private String generateSignature(String method, String uri, byte[] body, Map<String, String> headers) {
        try {
            StringBuilder signatureBase = new StringBuilder();
            signatureBase.append(method.toUpperCase()).append("\n");
            signatureBase.append(uri).append("\n");

            // Sort headers for consistent signature
            TreeMap<String, String> sortedHeaders = new TreeMap<>(headers);
            for (Map.Entry<String, String> entry : sortedHeaders.entrySet()) {
                if (entry.getKey().startsWith("X-")) {
                    signatureBase.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
                }
            }

            if (body != null && body.length > 0) {
                signatureBase.append(Base64.getEncoder().encodeToString(body));
            }

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(keySpec);

            byte[] signatureBytes = mac.doFinal(signatureBase.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC signature", e);
        }
    }
}
