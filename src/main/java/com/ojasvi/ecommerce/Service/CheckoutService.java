package com.ojasvi.ecommerce.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ojasvi.ecommerce.Entity.Address;
import com.ojasvi.ecommerce.Entity.Cart;
import com.ojasvi.ecommerce.Entity.CartItem;
import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Entity.OrderItem;
import com.ojasvi.ecommerce.Entity.Payment;
import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Repository.CartItemRepository;
import com.ojasvi.ecommerce.Repository.CartRepository;
import com.ojasvi.ecommerce.Repository.OrderItemRepository;
import com.ojasvi.ecommerce.Repository.OrderRepository;
import com.ojasvi.ecommerce.Repository.PaymentRepository;
import com.ojasvi.ecommerce.Repository.ProductRepository;
import com.ojasvi.ecommerce.Repository.UserRepository;

import java.math.BigDecimal;

import com.ojasvi.ecommerce.Enum.NotificationEvent;
import com.ojasvi.ecommerce.Enum.NotificationType;
import com.ojasvi.ecommerce.Enum.OrderStatus;
import com.ojasvi.ecommerce.Enum.PaymentMethod;
import com.ojasvi.ecommerce.Enum.PaymentStatus;
import com.ojasvi.ecommerce.Enum.RecipientType;
import com.ojasvi.ecommerce.Enum.ReferenceType;

@Service
public class CheckoutService {

	@Autowired
	private NotificationService notificationService;
	@Autowired
	private MailService mailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderItemRepository orderItemRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CartItemRepository cartItemRepository;
	@Autowired
	private PaymentRepository paymentRepository;

	@Transactional(rollbackFor = Exception.class)
	public Order placeOrderAfterSuccessfulPayment(
	        User user,
	        Address shippingAddress,
	        String razorpayOrderId,
	        String razorpayPaymentId,
	        String razorpaySignature,
	        BigDecimal shippingCharge,
	        String shippingMethod) {

	    if (user == null) {
	        throw new IllegalArgumentException("User cannot be null");
	    }

	    if (shippingAddress == null) {
	        throw new IllegalArgumentException("Shipping address cannot be null");
	    }

	    if (shippingCharge == null) {
	        shippingCharge = BigDecimal.ZERO;
	    }

	    if (shippingMethod == null ||
	            shippingMethod.isBlank()) {
	        shippingMethod = "standard";
	    }

	    Cart cart = cartRepository.findByUserId(user.getId())
	            .orElseThrow(() ->
	                    new IllegalStateException("Cart not found"));

	    List<CartItem> cartItems =
	            cartItemRepository.findByCartId(cart.getId());

	    if (cartItems.isEmpty()) {
	        throw new IllegalStateException("Cart is empty");
	    }

	    if (paymentRepository.existsByRazorpayPaymentId(
	            razorpayPaymentId)) {

	        throw new IllegalStateException(
	                "Payment already processed.");
	    }

	    //----------------------------------------------------
	    // CHECK STOCK FIRST
	    //----------------------------------------------------
	    for (CartItem item : cartItems) {

	        Product product = item.getProduct();

	        if (product.getStock() < item.getQuantity()) {
	            throw new IllegalStateException(
	                    product.getProductName()
	                            + " is out of stock");
	        }
	    }

	    //----------------------------------------------------
	    // CREATE ORDER
	    //----------------------------------------------------
	    Order order = new Order();

	    order.setOrderNumber(
	            "ORD-" + System.currentTimeMillis());

	    order.setCustomer(user);

	    order.setShippingAddress(
	            shippingAddress);

	    order.setPaymentMethod(
	            PaymentMethod.ONLINE);

	    order.setPaymentStatus(
	            PaymentStatus.SUCCESS);

	    order.setOrderStatus(
	            OrderStatus.PENDING);

	    order = orderRepository.save(order);

	    //----------------------------------------------------
	    // CREATE ORDER ITEMS
	    //----------------------------------------------------
	    BigDecimal subtotal = BigDecimal.ZERO;

	    for (CartItem item : cartItems) {

	        Product product = item.getProduct();

	        BigDecimal price =
	                product.getSellingPrice();

	        BigDecimal lineTotal =
	                price.multiply(
	                        BigDecimal.valueOf(
	                                item.getQuantity()));

	        OrderItem orderItem =
	                new OrderItem();

	        orderItem.setOrder(order);
	        orderItem.setProduct(product);
	        orderItem.setProductName(
	                product.getProductName());

	        if (product.getImages() != null) {

	            product.getImages()
	                    .stream()
	                    .filter(img ->
	                            Boolean.TRUE.equals(
	                                    img.getPrimaryImage()))
	                    .findFirst()
	                    .ifPresent(img ->
	                            orderItem.setProductImage(
	                                    img.getImageUrl()));
	        }

	        orderItem.setProductPrice(price);
	        orderItem.setQuantity(
	                item.getQuantity());
	        orderItem.setSubtotal(
	                lineTotal);

	        orderItemRepository.save(orderItem);

	        subtotal = subtotal.add(lineTotal);

	        //------------------------------------------------
	        // UPDATE STOCK
	        //------------------------------------------------
	        int remainingStock =
	                product.getStock()
	                        - item.getQuantity();

	        if (remainingStock < 0) {
	            throw new IllegalStateException(
	                    product.getProductName()
	                            + " is out of stock");
	        }

	        product.setStock(
	                remainingStock);

	        productRepository.save(product);
	    }

	    //----------------------------------------------------
	    // TOTAL CALCULATION
	    //----------------------------------------------------
	    BigDecimal discountAmount =
	            BigDecimal.ZERO;

	    BigDecimal taxAmount =
	            BigDecimal.ZERO;

	    BigDecimal grandTotal =
	            subtotal
	                    .add(shippingCharge)
	                    .add(taxAmount)
	                    .subtract(discountAmount);

	    order.setSubtotal(subtotal);
	    order.setShippingCharge(
	            shippingCharge);
	    order.setShippingMethod(
	            shippingMethod);
	    order.setDiscountAmount(
	            discountAmount);
	    order.setTaxAmount(
	            taxAmount);
	    order.setGrandTotal(
	            grandTotal);

	    order = orderRepository.save(order);

	    //----------------------------------------------------
	    // SAVE PAYMENT
	    //----------------------------------------------------
	    Payment payment = new Payment();

	    payment.setOrder(order);
	    payment.setAmount(grandTotal);
	    payment.setPaymentGateway(
	            "RAZORPAY");
	    payment.setPaymentMethod(
	            PaymentMethod.ONLINE);
	    payment.setPaymentStatus(
	            PaymentStatus.SUCCESS);

	    payment.setRazorpayOrderId(
	            razorpayOrderId);

	    payment.setRazorpayPaymentId(
	            razorpayPaymentId);

	    payment.setRazorpaySignature(
	            razorpaySignature);

	    paymentRepository.save(payment);

	    //----------------------------------------------------
	    // CLEAR CART
	    //----------------------------------------------------
	    cartItemRepository.deleteByCartId(
	            cart.getId());

	    cart.setTotalAmount(
	            BigDecimal.ZERO);

	    cart.setTotalItems(0);

	    cartRepository.save(cart);

	    //----------------------------------------------------
	    // CUSTOMER NOTIFICATION
	    //----------------------------------------------------
	    try {

	        notificationService.createNotification(
	                "Order Placed Successfully",
	                "Your order #"
	                        + order.getOrderNumber()
	                        + " has been placed successfully. "
	                        + "We have received your payment of ₹"
	                        + order.getGrandTotal()
	                        + ".",
	                NotificationType.ORDER,
	                NotificationEvent.ORDER_PLACED,
	                RecipientType.CUSTOMER,
	                user.getId(),
	                ReferenceType.ORDER,
	                order.getId());

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    //----------------------------------------------------
	    // ADMIN NOTIFICATION
	    //----------------------------------------------------
	    try {

	        User admin =
	                userRepository.findByRole_RoleName(
	                        "ADMIN")
	                        .orElseThrow(() ->
	                                new RuntimeException(
	                                        "Admin not found"));

	        notificationService.createNotification(
	                "New Order Received",
	                user.getFullName()
	                        + " has placed a new order #"
	                        + order.getOrderNumber()
	                        + " worth ₹"
	                        + order.getGrandTotal(),
	                NotificationType.ORDER,
	                NotificationEvent.NEW_ORDER,
	                RecipientType.ADMIN,
	                admin.getId(),
	                ReferenceType.ORDER,
	                order.getId());

	        //------------------------------------------------
	        // CUSTOMER EMAIL
	        //------------------------------------------------
	        try {
	            mailService.sendOrderPlacedEmail(
	                    user,
	                    order.getOrderNumber());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        //------------------------------------------------
	        // ADMIN EMAIL
	        //------------------------------------------------
	        try {
	            mailService.sendAdminNewOrderEmail(
	                    admin.getEmail(),
	                    order.getOrderNumber(),
	                    user.getFullName(),
	                    order.getGrandTotal()
	                            .doubleValue());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return order;
	}
}
