package com.ojasvi.ecommerce.Service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Entity.User;

@Service
public class MailService {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromMail;

	private String formatAmount(BigDecimal amount) {
		return amount != null ? amount.setScale(2).toPlainString() : "0.00";
	}

	// =====================================================
	// CUSTOMER ACCOUNT EMAILS
	// =====================================================

	public void sendCustomerWelcomeEmail(User customer) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);

		message.setTo(customer.getEmail());

		message.setSubject("Welcome to Ojasvi - Your Account Has Been Created");

		message.setText("Dear " + customer.getFullName() + ",\n\n"

				+ "Welcome to Ojasvi!\n\n"

				+ "Your account has been created successfully.\n\n"

				+ "You can now:\n"

				+ "• Browse premium home linen\n"

				+ "• Add products to wishlist\n"

				+ "• Place orders\n"

				+ "• Track deliveries\n\n"

				+ "Thank you for choosing Ojasvi.\n\n"

				+ "Regards,\n"

				+ "Team Ojasvi");

		mailSender.send(message);
	}

	public void sendCustomerLoginEmail(User customer) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);

		message.setTo(customer.getEmail());

		message.setSubject("Successful Login - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "Your Ojasvi account has been logged in successfully.\n\n"

						+ "If this login was performed by you, no further action is required.\n\n"

						+ "If you do not recognize this login, please change your password immediately.\n\n"

						+ "Regards,\n"

						+ "Team Ojasvi"

		);

		mailSender.send(message);
	}

	public void sendCustomerPasswordResetEmail(User customer) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);

		message.setTo(customer.getEmail());

		message.setSubject("Password Reset Successful - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "Your Ojasvi account password has been reset successfully.\n\n"

						+ "You can now log in using your new password.\n\n"

						+ "If you did not request this password reset, please contact our support team immediately.\n\n"

						+ "Regards,\n"

						+ "Team Ojasvi"

		);

		mailSender.send(message);
	}

	public void sendCustomerPasswordChangedEmail(User customer) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);

		message.setTo(customer.getEmail());

		message.setSubject("Password Changed Successfully - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "Your account password has been changed successfully.\n\n"

						+ "If you made this change, you can safely ignore this email.\n\n"

						+ "If you did not change your password, please reset it immediately and contact our support team.\n\n"

						+ "Regards,\n"

						+ "Team Ojasvi"

		);

		mailSender.send(message);
	}

	public void sendCustomerProfileUpdatedEmail(User customer) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);

		message.setTo(customer.getEmail());

		message.setSubject("Profile Updated - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "Your Ojasvi profile has been updated successfully.\n\n"

						+ "Your latest profile information is now available in your account.\n\n"

						+ "If you did not make these changes, please contact our support team immediately.\n\n"

						+ "Regards,\n"

						+ "Team Ojasvi"

		);

		mailSender.send(message);
	}

	public void sendCustomerEmailChangedEmail(User customer) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);

		message.setTo(customer.getEmail());

		message.setSubject("Email Address Updated - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "Your email address has been updated successfully.\n\n"

						+ "Your new email address is now linked to your Ojasvi account.\n\n"

						+ "If you did not make this change, please contact our support team immediately.\n\n"

						+ "Regards,\n"

						+ "Team Ojasvi"

		);

		mailSender.send(message);
	}

	// =====================================================
	// CUSTOMER ORDER EMAILS
	// =====================================================

	public void sendOrderPlacedEmail(User customer, String orderNumber) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Order Placed Successfully - Ojasvi");

		message.setText("Dear " + customer.getFullName() + ",\n\n"

				+ "Thank you for shopping with Ojasvi.\n\n"

				+ "Your order has been placed successfully.\n\n"

				+ "Order Number : #" + orderNumber + "\n\n"

				+ "We have received your order and will begin processing it shortly.\n\n"

				+ "You can track your order anytime from your account.\n\n"

				+ "Regards,\n" + "Team Ojasvi");

		mailSender.send(message);
	}

	public void sendOrderConfirmedEmail(User customer, Long orderId) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Your Order Has Been Confirmed - Ojasvi");

		message.setText("Dear " + customer.getFullName() + ",\n\n"

				+ "Great news!\n\n"

				+ "Your order has been confirmed.\n\n"

				+ "Order ID : #" + orderId + "\n\n"

				+ "Our team is preparing your order for dispatch.\n\n"

				+ "Thank you for choosing Ojasvi.\n\n"

				+ "Regards,\n" + "Team Ojasvi");

		mailSender.send(message);
	}

	public void sendOrderProcessingEmail(User customer, Long orderId) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Your Order Is Being Processed - Ojasvi");

		message.setText("Dear " + customer.getFullName() + ",\n\n"

				+ "Your order is currently being processed.\n\n"

				+ "Order ID : #" + orderId + "\n\n"

				+ "Our warehouse team is preparing your items for packing.\n\n"

				+ "We'll notify you once your order is packed and shipped.\n\n"

				+ "Regards,\n" + "Team Ojasvi");

		mailSender.send(message);
	}

	public void sendOrderPackedEmail(User customer, Long orderId) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Your Order Has Been Packed - Ojasvi");

		message.setText("Dear " + customer.getFullName() + ",\n\n"

				+ "Your order has been packed successfully.\n\n"

				+ "Order ID : #" + orderId + "\n\n"

				+ "It is now ready to be handed over to our delivery partner.\n\n"

				+ "Shipping details will be shared soon.\n\n"

				+ "Regards,\n" + "Team Ojasvi");

		mailSender.send(message);
	}

	public void sendOrderShippedEmail(User customer, Long orderId) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Your Order Has Been Shipped - Ojasvi");

		message.setText("Dear " + customer.getFullName() + ",\n\n"

				+ "Good news!\n\n"

				+ "Your order has been shipped.\n\n"

				+ "Order ID : #" + orderId + "\n\n"

				+ "Your package is now on its way.\n\n"

				+ "You can track the shipment from your Ojasvi account.\n\n"

				+ "Regards,\n" + "Team Ojasvi");

		mailSender.send(message);
	}

	public void sendOutForDeliveryEmail(User customer, Long orderId) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Your Order Is Out For Delivery - Ojasvi");

		message.setText("Dear " + customer.getFullName() + ",\n\n"

				+ "Exciting news!\n\n"

				+ "Your order is out for delivery today.\n\n"

				+ "Order ID : #" + orderId + "\n\n"

				+ "Please keep your phone available as our delivery partner may contact you.\n\n"

				+ "Thank you for shopping with Ojasvi.\n\n"

				+ "Regards,\n" + "Team Ojasvi");

		mailSender.send(message);
	}

	public void sendOrderDeliveredEmail(User customer, Long orderId) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Your Order Has Been Delivered - Ojasvi");

		message.setText("Dear " + customer.getFullName() + ",\n\n"

				+ "Your order has been delivered successfully.\n\n"

				+ "Order ID : #" + orderId + "\n\n"

				+ "We hope you enjoy your purchase.\n\n"

				+ "Thank you for choosing Ojasvi. We'd love to hear your feedback.\n\n"

				+ "Regards,\n" + "Team Ojasvi");

		mailSender.send(message);
	}

	public void sendOrderCancelledEmail(User customer, Long orderId) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Order Cancelled - Ojasvi");

		message.setText("Dear " + customer.getFullName() + ",\n\n"

				+ "Your order has been cancelled.\n\n"

				+ "Order ID : #" + orderId + "\n\n"

				+ "If you requested this cancellation, no further action is required.\n\n"

				+ "If the cancellation was unexpected, please contact our support team.\n\n"

				+ "We hope to serve you again soon.\n\n"

				+ "Regards,\n" + "Team Ojasvi");

		mailSender.send(message);
	}

	// =====================================================
	// PAYMENT EMAILS
	// =====================================================

	public void sendPaymentSuccessEmail(User customer, Order order) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Payment Successful - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "We have successfully received your payment.\n\n"

						+ "Order Number : " + order.getOrderNumber() + "\n" + "Amount Paid  : ₹"
						+ formatAmount(order.getGrandTotal()) + "\n\n"

						+ "Your order is now being processed.\n\n"

						+ "Thank you for shopping with Ojasvi.\n\n"

						+ "Regards,\n" + "Team Ojasvi"

		);

		mailSender.send(message);
	}

	public void sendPaymentFailedEmail(User customer, Order order) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Payment Failed - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "Unfortunately, your payment could not be completed.\n\n"

						+ "Order Number : " + order.getOrderNumber() + "\n" + "Amount       : ₹"
						+ formatAmount(order.getGrandTotal()) + "\n\n"

						+ "Please try again using another payment method.\n"

						+ "If any amount has been deducted, it will be automatically refunded according to your bank's policy.\n\n"

						+ "Regards,\n" + "Team Ojasvi"

		);

		mailSender.send(message);
	}

	public void sendPaymentPendingEmail(User customer, Order order) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Payment Pending - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "Your payment is currently pending confirmation.\n\n"

						+ "Order Number : " + order.getOrderNumber() + "\n" + "Amount       : ₹"
						+ formatAmount(order.getGrandTotal()) + "\n\n"

						+ "We are waiting for confirmation from the payment gateway.\n"

						+ "Once your payment is verified, we will notify you immediately.\n\n"

						+ "Regards,\n" + "Team Ojasvi"

		);

		mailSender.send(message);
	}

	public void sendRefundInitiatedEmail(User customer, Order order) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Refund Initiated - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "Your refund request has been approved.\n\n"

						+ "Order Number  : " + order.getOrderNumber() + "\n" + "Refund Amount : ₹"
						+ formatAmount(order.getGrandTotal()) + "\n\n"

						+ "Your refund has been initiated successfully.\n"

						+ "The refunded amount will be credited to your original payment method within 3-7 business days.\n\n"

						+ "Regards,\n" + "Team Ojasvi"

		);

		mailSender.send(message);
	}

	public void sendRefundCompletedEmail(User customer, Order order) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Refund Completed - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "Good news! Your refund has been successfully processed.\n\n"

						+ "Order Number  : " + order.getOrderNumber() + "\n" + "Refund Amount : ₹"
						+ formatAmount(order.getGrandTotal()) + "\n\n"

						+ "The refunded amount has been credited to your original payment method.\n\n"

						+ "Thank you for shopping with Ojasvi.\n\n"

						+ "Regards,\n" + "Team Ojasvi"

		);

		mailSender.send(message);
	}

	public void sendReturnRequestedEmail(User customer, Order order) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Return Request Received - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "We have successfully received your return request.\n\n"

						+ "Order Number : " + order.getOrderNumber() + "\n\n"

						+ "Our team will review your request and update you shortly.\n\n"

						+ "You can track the status of your return from your Ojasvi account.\n\n"

						+ "Regards,\n" + "Team Ojasvi"

		);

		mailSender.send(message);
	}

	public void sendReturnApprovedEmail(User customer, Order order) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Return Request Approved - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "Good news! Your return request has been approved.\n\n"

						+ "Order Number : " + order.getOrderNumber() + "\n\n"

						+ "Please keep the product ready for pickup. Our delivery partner will contact you shortly.\n\n"

						+ "After the product is successfully received and verified, your refund will be processed.\n\n"

						+ "Regards,\n" + "Team Ojasvi"

		);

		mailSender.send(message);
	}

	public void sendReturnRejectedEmail(User customer, Order order, String reason) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(customer.getEmail());

		message.setSubject("Return Request Update - Ojasvi");

		message.setText(

				"Dear " + customer.getFullName() + ",\n\n"

						+ "We regret to inform you that your return request could not be approved.\n\n"

						+ "Order Number : " + order.getOrderNumber() + "\n\n"

						+ "Reason : "
						+ (reason != null && !reason.isBlank() ? reason
								: "The return request does not meet our return policy.")
						+ "\n\n"

						+ "If you have any questions, please contact our support team.\n\n"

						+ "Regards,\n" + "Team Ojasvi"

		);

		mailSender.send(message);
	}

	// =====================================================
	// ADMIN EMAILS
	// =====================================================

	public void sendAdminNewCustomerEmail(String adminEmail, User customer) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);

		message.setTo(adminEmail);

		message.setSubject("New Customer Registration");

		message.setText(

				"Hello Admin,\n\n"

						+ "A new customer has registered.\n\n"

						+ "Customer Details\n\n"

						+ "Name : " + customer.getFullName() + "\n"

						+ "Email : " + customer.getEmail() + "\n"

						+ "Mobile : " + customer.getMobile() + "\n\n"

						+ "Please login to Admin Panel.\n\n"

						+ "Regards,\n"

						+ "Ojasvi System"

		);

		mailSender.send(message);

	}

	public void sendAdminNewOrderEmail(String adminEmail, String orderNumber, String customerName, double amount) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(adminEmail);
		message.setSubject("New Order Received");

		message.setText("Hello Admin,\n\n"

				+ "A new order has been placed.\n\n"

				+ "Order Details\n\n"

				+ "Order Number : " + orderNumber + "\n" + "Customer     : " + customerName + "\n" + "Amount       : ₹"
				+ amount + "\n\n"

				+ "Please login to the Admin Panel for more details.\n\n"

				+ "Regards,\n" + "Ojasvi System");

		mailSender.send(message);
	}

	public void sendAdminOrderCancelledEmail(String adminEmail, Order order) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(adminEmail);

		message.setSubject("Order Cancelled By Customer");

		message.setText(

				"Hello Admin,\n\n"

						+ "A customer has cancelled an order.\n\n"

						+ "Order Details\n\n"

						+ "Order Number : " + order.getOrderNumber() + "\n" + "Customer     : "
						+ order.getCustomer().getFullName() + "\n" + "Email        : " + order.getCustomer().getEmail()
						+ "\n" + "Amount       : ₹" + formatAmount(order.getGrandTotal()) + "\n" + "Payment Mode : "
						+ (order.getPaymentMethod() != null ? order.getPaymentMethod().name() : "N/A") + "\n\n"

						+ "Please login to the Admin Panel for more details.\n\n"

						+ "Regards,\n" + "Ojasvi System"

		);

		mailSender.send(message);
	}

	public void sendAdminPaymentReceivedEmail(String adminEmail, String orderNumber, String customerName,
			double amount) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(adminEmail);
		message.setSubject("Payment Received");

		message.setText("Hello Admin,\n\n"

				+ "A payment has been received successfully.\n\n"

				+ "Payment Details\n\n"

				+ "Order Number : " + orderNumber + "\n" + "Customer     : " + customerName + "\n" + "Amount       : ₹"
				+ amount + "\n\n"

				+ "Please login to the Admin Panel for more details.\n\n"

				+ "Regards,\n" + "Ojasvi System");

		mailSender.send(message);
	}

	public void sendAdminReturnRequestEmail(String adminEmail, String orderNumber, String customerName, String reason) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(adminEmail);
		message.setSubject("New Return Request");

		message.setText("Hello Admin,\n\n"

				+ "A customer has requested a return.\n\n"

				+ "Return Details\n\n"

				+ "Order Number : " + orderNumber + "\n" + "Customer     : " + customerName + "\n" + "Reason       : "
				+ reason + "\n\n"

				+ "Please review the request in the Admin Panel.\n\n"

				+ "Regards,\n" + "Ojasvi System");

		mailSender.send(message);
	}

	public void sendAdminRefundRequestEmail(String adminEmail, String orderNumber, String customerName, double amount) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(adminEmail);
		message.setSubject("New Refund Request");

		message.setText("Hello Admin,\n\n"

				+ "A customer has requested a refund.\n\n"

				+ "Refund Details\n\n"

				+ "Order Number : " + orderNumber + "\n" + "Customer     : " + customerName + "\n" + "Refund Amount: ₹"
				+ amount + "\n\n"

				+ "Please review the refund request in the Admin Panel.\n\n"

				+ "Regards,\n" + "Ojasvi System");

		mailSender.send(message);
	}

	public void sendAdminLowStockEmail(String adminEmail, String productName, int availableQuantity) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(adminEmail);
		message.setSubject("Low Stock Alert");

		message.setText("Hello Admin,\n\n"

				+ "A product is running low on stock.\n\n"

				+ "Product Details\n\n"

				+ "Product Name : " + productName + "\n" + "Available Qty: " + availableQuantity + "\n\n"

				+ "Please replenish the inventory.\n\n"

				+ "Regards,\n" + "Ojasvi System");

		mailSender.send(message);
	}
	
	public void sendAdminOutOfStockEmail(String adminEmail,
            String productName) {

SimpleMailMessage message = new SimpleMailMessage();

message.setFrom(fromMail);
message.setTo(adminEmail);

message.setSubject("Out of Stock Alert");

message.setText(

"Hello Admin,\n\n"

+ "A product is currently out of stock.\n\n"

+ "Product Details\n\n"

+ "Product Name : " + productName + "\n"
+ "Available Qty: 0\n\n"

+ "Customers will not be able to purchase this product until inventory is replenished.\n\n"

+ "Please restock the product as soon as possible.\n\n"

+ "Regards,\n"
+ "Ojasvi System"

);

mailSender.send(message);
}

	public void sendAdminContactMessageEmail(String adminEmail, String customerName, String customerEmail,
			String subject) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(adminEmail);
		message.setSubject("New Contact Message");

		message.setText("Hello Admin,\n\n"

				+ "A new contact enquiry has been received.\n\n"

				+ "Contact Details\n\n"

				+ "Name    : " + customerName + "\n" + "Email   : " + customerEmail + "\n" + "Subject : " + subject
				+ "\n\n"

				+ "Please login to the Admin Panel to view the complete message.\n\n"

				+ "Regards,\n" + "Ojasvi System");

		mailSender.send(message);
	}

	public void sendAdminNewReviewEmail(String adminEmail, String customerName, String productName, int rating) {

		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromMail);
		message.setTo(adminEmail);
		message.setSubject("New Product Review");

		message.setText("Hello Admin,\n\n"

				+ "A new product review has been submitted.\n\n"

				+ "Review Details\n\n"

				+ "Customer : " + customerName + "\n" + "Product  : " + productName + "\n" + "Rating   : " + rating
				+ "/5\n\n"

				+ "Please login to the Admin Panel to review it.\n\n"

				+ "Regards,\n" + "Ojasvi System");

		mailSender.send(message);
	}
	
	public void sendAdminProductRestockedEmail(
	        String adminEmail,
	        String productName,
	        int availableQuantity) {

	    SimpleMailMessage message = new SimpleMailMessage();

	    message.setFrom(fromMail);
	    message.setTo(adminEmail);

	    message.setSubject("Product Restocked");

	    message.setText(

	            "Hello Admin,\n\n"

	                    + "A previously out-of-stock product has been restocked.\n\n"

	                    + "Product Details\n\n"

	                    + "Product Name : " + productName + "\n"
	                    + "Available Qty: " + availableQuantity + "\n\n"

	                    + "The product is now available for customers to purchase again.\n\n"

	                    + "Regards,\n"
	                    + "Ojasvi System"

	    );

	    mailSender.send(message);
	}

}
