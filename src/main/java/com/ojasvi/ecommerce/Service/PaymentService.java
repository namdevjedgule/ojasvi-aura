package com.ojasvi.ecommerce.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Entity.Payment;
import com.ojasvi.ecommerce.Enum.PaymentMethod;
import com.ojasvi.ecommerce.Enum.PaymentStatus;
import com.ojasvi.ecommerce.Repository.PaymentRepository;
import com.razorpay.RazorpayClient;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Create Razorpay Order
     */
    public JSONObject createRazorpayOrder(BigDecimal amount) throws Exception {

        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();

        // Razorpay accepts amount in paise
        options.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue());

        options.put("currency", "INR");

        options.put("receipt", "OJASVI_" + System.currentTimeMillis());

        options.put("payment_capture", 1);

        return razorpay.orders.create(options).toJson();
    }

    /**
     * Verify Razorpay Signature
     */
    public boolean verifySignature(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature) {

        try {

            String payload = razorpayOrderId + "|" + razorpayPaymentId;

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            keySecret.getBytes(StandardCharsets.UTF_8),
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

    /**
     * Save Successful Payment
     */
    public Payment saveSuccessfulPayment(
            Order order,
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature,
            String paymentMode) {

        Payment payment = new Payment();

        payment.setOrder(order);

        payment.setAmount(order.getGrandTotal());

        payment.setPaymentGateway("RAZORPAY");

        payment.setPaymentMethod(PaymentMethod.ONLINE);

        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        payment.setPaymentMode(paymentMode);

        payment.setRazorpayOrderId(razorpayOrderId);

        payment.setRazorpayPaymentId(razorpayPaymentId);

        payment.setRazorpaySignature(razorpaySignature);

        return paymentRepository.save(payment);
    }

    /**
     * Save Failed Payment
     */
    public Payment saveFailedPayment(
            Order order,
            String razorpayOrderId,
            String failureReason) {

        Payment payment = new Payment();

        payment.setOrder(order);

        payment.setAmount(order.getGrandTotal());

        payment.setPaymentGateway("RAZORPAY");

        payment.setPaymentMethod(PaymentMethod.ONLINE);

        payment.setPaymentStatus(PaymentStatus.FAILED);

        payment.setRazorpayOrderId(razorpayOrderId);

        payment.setFailureReason(failureReason);

        return paymentRepository.save(payment);
    }

    /**
     * Find Payment By Order
     */
    public Optional<Payment> findByOrder(Order order) {

        return paymentRepository.findByOrder(order);
    }

    /**
     * Find By Razorpay Order Id
     */
    public Optional<Payment> findByRazorpayOrderId(
            String razorpayOrderId) {

        return paymentRepository.findByRazorpayOrderId(
                razorpayOrderId);
    }

    /**
     * Find By Razorpay Payment Id
     */
    public Optional<Payment> findByRazorpayPaymentId(
            String razorpayPaymentId) {

        return paymentRepository.findByRazorpayPaymentId(
                razorpayPaymentId);
    }

    /**
     * Duplicate Payment Check
     */
    public boolean isPaymentAlreadyProcessed(
            String razorpayPaymentId) {

        return paymentRepository.existsByRazorpayPaymentId(
                razorpayPaymentId);
    }

    /**
     * Update Payment Status
     */
    public Payment updatePaymentStatus(
            Payment payment,
            PaymentStatus paymentStatus) {

        payment.setPaymentStatus(paymentStatus);

        return paymentRepository.save(payment);
    }

    /**
     * Future Refund Support
     */
    public void refundPayment(
            String razorpayPaymentId,
            BigDecimal amount) {

        // Razorpay Refund API
        // Will implement later
    }

    /**
     * Razorpay Key for Frontend
     */
    public String getKeyId() {

        return keyId;
    }

}