package com.ojasvi.ecommerce.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    /**
     * Create Razorpay Order
     */
    public Order createRazorpayOrder(BigDecimal amount) throws Exception {

        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();

        // Razorpay expects amount in paise
        options.put("amount", amount.multiply(BigDecimal.valueOf(100)));

        options.put("currency", "INR");

        options.put("receipt", "receipt_" + System.currentTimeMillis());

        options.put("payment_capture", 1);

        return razorpay.orders.create(options);
    }

    /**
     * Verify Razorpay Signature
     */
    public boolean verifySignature(String razorpayOrderId,
                                   String razorpayPaymentId,
                                   String razorpaySignature) {

        try {

            String payload = razorpayOrderId + "|" + razorpayPaymentId;

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKey = new SecretKeySpec(
                    keySecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256");

            sha256Hmac.init(secretKey);

            byte[] hash = sha256Hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder generatedSignature = new StringBuilder();

            for (byte b : hash) {
                generatedSignature.append(String.format("%02x", b));
            }

            return generatedSignature.toString().equals(razorpaySignature);

        } catch (Exception e) {

            return false;
        }
    }

}
