package com.ojasvi.ecommerce.Service;

import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayWebhookService {

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public boolean verify(String payload, String razorpaySignature) {

        try {

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            webhookSecret.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256");

            sha256Hmac.init(secretKey);

            byte[] hash =
                    sha256Hmac.doFinal(
                            payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder generatedSignature = new StringBuilder();

            for (byte b : hash) {
                generatedSignature.append(String.format("%02x", b));
            }

            return generatedSignature
                    .toString()
                    .equals(razorpaySignature);

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }
    }
}