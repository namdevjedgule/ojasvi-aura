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

	@PostMapping("/checkout/place-order")
	@ResponseBody
	public ResponseEntity<?> placeOrder(

			@RequestParam(required = false) Long addressId,
			@RequestParam(required = false) String addressLine1,
			@RequestParam(required = false) String addressLine2,
			@RequestParam(required = false) String landmark,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String state, 
			@RequestParam(required = false) String country,
			@RequestParam(required = false) String pincode, 
			@RequestParam(required = false) String addressType,
			@RequestParam(value = "saveAddress", required = false) Boolean saveAddress,
			@RequestParam String paymentMethod,

			HttpSession session) {

		User user = SessionUtil.getLoggedInUser(session);

		if (user == null) {

			return ResponseEntity.ok(Map.of("success", false, "message", "Please login first"));
		}

		Address shippingAddress;

		if (addressId != null) {

			shippingAddress = addressService.getById(addressId);

			if (!shippingAddress.getUser().getId().equals(user.getId())) {

				return ResponseEntity.ok(Map.of("success", false, "message", "Invalid address"));
			}

		}

		else {

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

				List<Address> existing = addressService.getUserAddresses(user.getId());

				if (existing.isEmpty()) {

					shippingAddress.setDefaultAddress(true);

				} else {

					shippingAddress.setDefaultAddress(false);

				}

				shippingAddress = addressService.saveAddress(shippingAddress, user);

			} else {
				shippingAddress = addressService.saveAddressForOrder(shippingAddress, user);

			}

		}

		Order order = checkoutService.placeOrder(user, shippingAddress, paymentMethod);

		return ResponseEntity.ok(

				Map.of("success", true, "message", "Order placed successfully", "redirectUrl",
						"/order-success/" + order.getOrderNumber()));

	}

	@GetMapping("/order-success/{orderNumber}")
	public String orderSuccess(@PathVariable String orderNumber, Model model) {

		Order order = orderService.findByOrderNumber(orderNumber);
		
		 List<OrderItem> orderItems =
		            orderItemService.getByOrderId(order.getId());

	    model.addAttribute("order", order);
	    model.addAttribute("orderNumber", order.getOrderNumber());
	    model.addAttribute("shippingAddress", order.getShippingAddress());
	    model.addAttribute("paymentMethod", order.getPaymentMethod().name());
	    model.addAttribute("grandTotal", order.getGrandTotal());
	    model.addAttribute("orderItems", orderItems);

		return "order-success";
	}
}
