package com.ojasvi.ecommerce.Controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ojasvi.ecommerce.Service.PaymentService;
import com.ojasvi.ecommerce.Service.RazorpayWebhookService;

@RestController
@RequestMapping("/razorpay")
public class RazorpayWebhookController {

    @Autowired
    private RazorpayWebhookService webhookService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(

            @RequestHeader("X-Razorpay-Signature")
            String signature,

            @RequestBody
            String payload) {

        try {

            System.out.println("\n================= RAZORPAY WEBHOOK =================");

            //----------------------------------------------------
            // Verify Signature
            //----------------------------------------------------

            boolean verified = webhookService.verify(payload, signature);

            if (!verified) {

                System.out.println("Webhook Signature Invalid");

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Invalid Signature");
            }

            System.out.println("Webhook Signature Verified");

            //----------------------------------------------------
            // Read JSON
            //----------------------------------------------------

            JSONObject json = new JSONObject(payload);

            String event = json.getString("event");

            System.out.println("Event : " + event);

            //----------------------------------------------------
            // Process payment events only
            //----------------------------------------------------

            if ("payment.captured".equals(event)
                    || "order.paid".equals(event)) {

                JSONObject payment =
                        json.getJSONObject("payload")
                                .getJSONObject("payment")
                                .getJSONObject("entity");

                String paymentId =
                        payment.getString("id");

                String orderId =
                        payment.getString("order_id");

                String status =
                        payment.getString("status");

                String method =
                        payment.optString("method");

                String email =
                        payment.optString("email");

                String contact =
                        payment.optString("contact");

                long amount =
                        payment.getLong("amount");

                System.out.println("------------------------------------");
                System.out.println("Payment Id : " + paymentId);
                System.out.println("Order Id   : " + orderId);
                System.out.println("Status     : " + status);
                System.out.println("Method     : " + method);
                System.out.println("Amount     : " + amount);
                System.out.println("Email      : " + email);
                System.out.println("Contact    : " + contact);
                System.out.println("------------------------------------");

                //----------------------------------------------------
                // Duplicate Check
                //----------------------------------------------------

                if (paymentService.isPaymentAlreadyProcessed(paymentId)) {

                    System.out.println("Duplicate Webhook Ignored");

                    return ResponseEntity.ok("Already Processed");
                }

                //----------------------------------------------------
                // PHASE-1
                // Just log the payment.
                // We'll process order creation in next phase.
                //----------------------------------------------------

                System.out.println("Payment Received Successfully");

            } else {

                System.out.println("Ignored Event : " + event);

            }

            System.out.println("================ END WEBHOOK ================\n");

            return ResponseEntity.ok("OK");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Webhook Error");
        }
    }

}