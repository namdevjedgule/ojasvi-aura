package com.ojasvi.ecommerce.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ojasvi.ecommerce.Entity.Address;
import com.ojasvi.ecommerce.Entity.Cart;
import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Entity.OrderItem;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.SessionUtil;
import com.ojasvi.ecommerce.Service.AddressService;
import com.ojasvi.ecommerce.Service.CartService;
import com.ojasvi.ecommerce.Service.CheckoutService;
import com.ojasvi.ecommerce.Service.OrderService;
import com.ojasvi.ecommerce.Service.PaymentService;
import com.ojasvi.ecommerce.Service.ShippingService;
import com.ojasvi.ecommerce.Service.OrderItemService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

	@Autowired
	private CartService cartService;

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private AddressService addressService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderItemService orderItemService;
	
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private ShippingService shippingService;

	@Autowired
	private HttpSession session;

	@GetMapping("/checkout")
	public String checkout(Model model) {

		User user = SessionUtil.getLoggedInUser(session);

		if (user == null) {
			return "redirect:/login";
		}

		Cart cart = cartService.getCartByUser(user);

		if (cart.getTotalItems() == null || cart.getTotalItems() == 0) {
			return "redirect:/cart";
		}

		BigDecimal shippingCharge = BigDecimal.ZERO;

		Address defaultAddress =
		        addressService.getDefaultAddress(user);

		if (defaultAddress != null) {

			Double shipping =
					shippingService.calculateShipping(
					        defaultAddress.getCity(),
					        defaultAddress.getState(),
					        defaultAddress.getCountry(),
					        "standard",
					        cart.getTotalAmount().doubleValue());

		    shippingCharge =
		            BigDecimal.valueOf(
		                    shipping == null ? 0 : shipping);
		}
		BigDecimal couponDiscount = BigDecimal.ZERO;
		BigDecimal codCharge = BigDecimal.ZERO;

		BigDecimal grandTotal = cart.getTotalAmount().add(shippingCharge).add(codCharge).subtract(couponDiscount);

		model.addAttribute("user", user);
		model.addAttribute("cart", cart);
		model.addAttribute("cartItems", cartService.getCartItems(user));

		model.addAttribute("totalAmount", cart.getTotalAmount());
		model.addAttribute("shippingCharge", shippingCharge);
		model.addAttribute("couponDiscount", couponDiscount);
		model.addAttribute("codCharge", codCharge);
		model.addAttribute("grandTotal", grandTotal);

		List<Address> savedAddresses = addressService.getUserAddresses(user.getId());

		model.addAttribute("savedAddresses", savedAddresses);

		model.addAttribute("user", user);

		return "checkout";
	}

	@PostMapping("/checkout/create-payment-order")
	@ResponseBody
	public ResponseEntity<?> createPaymentOrder(

	        @RequestParam(required = false) Long addressId,
	        @RequestParam(required = false) String addressLine1,
	        @RequestParam(required = false) String addressLine2,
	        @RequestParam(required = false) String landmark,
	        @RequestParam(required = false) String city,
	        @RequestParam(required = false) String state,
	        @RequestParam(required = false) String country,
	        @RequestParam(required = false) String pincode,
	        @RequestParam(required = false) String addressType,
	        @RequestParam(defaultValue = "standard") String shippingMethod,
	        @RequestParam(value = "saveAddress", required = false) Boolean saveAddress) {

	    try {

	        User user = SessionUtil.getLoggedInUser(session);

	        if (user == null) {

	            return ResponseEntity.ok(
	                    Map.of(
	                            "success", false,
	                            "message", "Please login first"));
	        }

	        Address shippingAddress;

	        if (addressId != null) {

	            shippingAddress = addressService.getById(addressId);

	            if (shippingAddress == null ||
	                    !shippingAddress.getUser()
	                            .getId()
	                            .equals(user.getId())) {

	                return ResponseEntity.ok(
	                        Map.of(
	                                "success", false,
	                                "message", "Invalid address"));
	            }

	        } else {

	            shippingAddress = new Address();

	            shippingAddress.setUser(user);
	            shippingAddress.setAddressLine1(addressLine1);
	            shippingAddress.setAddressLine2(addressLine2);
	            shippingAddress.setLandmark(landmark);
	            shippingAddress.setCity(city);
	            shippingAddress.setState(state);
	            shippingAddress.setCountry(country);
	            shippingAddress.setPincode(pincode);
	            shippingAddress.setAddressType(addressType);

	            if (Boolean.TRUE.equals(saveAddress)) {

	                List<Address> existing =
	                        addressService.getUserAddresses(user.getId());

	                shippingAddress.setDefaultAddress(existing.isEmpty());

	                shippingAddress =
	                        addressService.saveAddress(shippingAddress, user);

	            } else {

	                shippingAddress =
	                        addressService.saveAddressForOrder(
	                                shippingAddress,
	                                user);
	            }
	        }

	        Cart cart = cartService.getCartByUser(user);
	        
	        if (cart == null ||
	                cart.getTotalItems() == null ||
	                cart.getTotalItems() == 0) {

	            return ResponseEntity.ok(
	                    Map.of(
	                            "success", false,
	                            "message",
	                            "Your cart is empty"));
	        }
	        
	        Double shipping =
	        		shippingService.calculateShipping(
	        		        shippingAddress.getCity(),
	        		        shippingAddress.getState(),
	        		        shippingAddress.getCountry(),
	        		        shippingMethod,
	        		        cart.getTotalAmount().doubleValue());

	        BigDecimal shippingCharge =
	                BigDecimal.valueOf(
	                        shipping == null ? 0 : shipping);

	        BigDecimal grandTotal =
	                cart.getTotalAmount()
	                    .add(shippingCharge);

	        var razorpayOrder = paymentService.createRazorpayOrder(
	                grandTotal);

	        System.out.println(razorpayOrder.toString(2));
	        
	        session.setAttribute("CHECKOUT_ADDRESS_ID", shippingAddress.getId());
	        session.setAttribute("RAZORPAY_ORDER_ID", razorpayOrder.getString("id"));
	        session.setAttribute("SHIPPING_CHARGE", shippingCharge);
	        session.setAttribute("SHIPPING_METHOD", shippingMethod);
	        session.setAttribute("GRAND_TOTAL", grandTotal);

	        return ResponseEntity.ok(
	                Map.of(
	                        "success", true,
	                        "key", paymentService.getKeyId(),
	                        "orderId", razorpayOrder.getString("id"),
	                        "amount", razorpayOrder.get("amount"),
	                        "currency", razorpayOrder.getString("currency")));

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity.ok(
	                Map.of(
	                        "success", false,
	                        "message", e.getMessage()));
	    }
	}
	
	@PostMapping("/checkout/verify-payment")
	@ResponseBody
	public ResponseEntity<?> verifyPayment(

	        @RequestParam String razorpayOrderId,
	        @RequestParam String razorpayPaymentId,
	        @RequestParam String razorpaySignature) {

	    try {

	        User user = SessionUtil.getLoggedInUser(session);

	        if (user == null) {

	            return ResponseEntity.ok(
	                    Map.of(
	                            "success", false,
	                            "message", "Please login first"));
	        }
	        
	        String sessionOrderId =
	                (String) session.getAttribute("RAZORPAY_ORDER_ID");

	        if (sessionOrderId == null ||
	                !sessionOrderId.equals(razorpayOrderId)) {

	            return ResponseEntity.ok(
	                    Map.of(
	                            "success", false,
	                            "message", "Invalid payment request"));
	        }

	        boolean verified =
	                paymentService.verifySignature(
	                        razorpayOrderId,
	                        razorpayPaymentId,
	                        razorpaySignature);

	        if (!verified) {

	            return ResponseEntity.ok(
	                    Map.of(
	                            "success", false,
	                            "message", "Payment verification failed"));
	        }

	        Long addressId =
	                (Long) session.getAttribute("CHECKOUT_ADDRESS_ID");

	        if (addressId == null) {

	            return ResponseEntity.ok(
	                    Map.of(
	                            "success", false,
	                            "message", "Shipping address not found"));
	        }

	        Address shippingAddress =
	                addressService.getById(addressId);
	        
	        BigDecimal shippingCharge =
	                (BigDecimal) session.getAttribute(
	                        "SHIPPING_CHARGE");

	        String shippingMethod =
	                (String) session.getAttribute(
	                        "SHIPPING_METHOD");

	        BigDecimal grandTotal =
	                (BigDecimal) session.getAttribute(
	                        "GRAND_TOTAL");

	        Order order =
	                checkoutService
	                        .placeOrderAfterSuccessfulPayment(
	                                user,
	                                shippingAddress,
	                                razorpayOrderId,
	                                razorpayPaymentId,
	                                razorpaySignature,
	                                shippingCharge,
	                                shippingMethod);

	        session.removeAttribute("CHECKOUT_ADDRESS_ID");
	        session.removeAttribute("RAZORPAY_ORDER_ID");
	        session.removeAttribute("SHIPPING_CHARGE");
	        session.removeAttribute("SHIPPING_METHOD");
	        session.removeAttribute("GRAND_TOTAL");

	        return ResponseEntity.ok(
	                Map.of(
	                        "success", true,
	                        "redirectUrl",
	                        "/order-success/" + order.getOrderNumber()));

	    } catch (Exception e) {
	    	
	    	 e.printStackTrace();

	        return ResponseEntity.ok(
	                Map.of(
	                        "success", false,
	                        "message", e.getMessage()));
	    }
	}

	@GetMapping("/order-success/{orderNumber}")
	public String orderSuccess(@PathVariable String orderNumber, Model model) {
		
		User user = SessionUtil.getLoggedInUser(session);

	    if (user == null) {
	        return "redirect:/login";
	    }

		Order order = orderService.findByOrderNumber(orderNumber);
		
		if (order == null ||
		        !order.getCustomer().getId().equals(user.getId())) {

		        return "redirect:/customer/orders";
		    }
		
		 List<OrderItem> orderItems =
		            orderItemService.getByOrderId(order.getId());
		 
		 String estimatedDelivery;

		    if ("express".equalsIgnoreCase(order.getShippingMethod())) {
		        estimatedDelivery = "1–2 Business Days";
		    } else {
		        estimatedDelivery = "5–7 Business Days";
		    }

	    model.addAttribute("order", order);
	    model.addAttribute("orderNumber", order.getOrderNumber());
	    model.addAttribute("shippingAddress", order.getShippingAddress());
	    model.addAttribute("paymentMethod", order.getPaymentMethod().name());
	    model.addAttribute("grandTotal", order.getGrandTotal());
	    model.addAttribute("orderItems", orderItems);
	    model.addAttribute("estimatedDelivery", estimatedDelivery);
	    model.addAttribute(
	            "estimatedDeliveryDate",
	            order.getEstimatedDeliveryDate());

		return "order-success";
	}
	
	@PostMapping("/checkout/calculate-shipping")
	@ResponseBody
	public ResponseEntity<?> calculateShipping(
	        @RequestParam String shippingMethod,
	        @RequestParam(required = false) Long addressId,
	        @RequestParam(required = false) String state,
	        @RequestParam(required = false) String country) {

	    try {

	        User user = SessionUtil.getLoggedInUser(session);

	        if (user == null) {
	            return ResponseEntity.ok(
	                    Map.of(
	                            "success", false,
	                            "message", "Please login"));
	        }

	        String stateName = state;
	        String city = "";
	        String countryName = country;

	        if (addressId != null) {

	            Address address = addressService.getById(addressId);

	            city = address.getCity();
	            stateName = address.getState();
	            countryName = address.getCountry();
	        }

	        Cart cart = cartService.getCartByUser(user);

	        Double shipping =
	                shippingService.calculateShipping(
	                        city,
	                        stateName,
	                        countryName,
	                        shippingMethod,
	                        cart.getTotalAmount().doubleValue());

	        BigDecimal shippingCharge =
	                BigDecimal.valueOf(
	                        shipping == null ? 0 : shipping);

	        BigDecimal grandTotal =
	                cart.getTotalAmount()
	                        .add(shippingCharge);

	        return ResponseEntity.ok(
	                Map.of(
	                        "success", true,
	                        "shippingCharge", shippingCharge,
	                        "grandTotal", grandTotal));

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity.ok(
	                Map.of(
	                        "success", false,
	                        "message", e.getMessage()));
	    }
	}
}
