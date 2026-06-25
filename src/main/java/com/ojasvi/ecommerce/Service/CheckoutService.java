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
import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Repository.CartItemRepository;
import com.ojasvi.ecommerce.Repository.CartRepository;
import com.ojasvi.ecommerce.Repository.OrderItemRepository;
import com.ojasvi.ecommerce.Repository.OrderRepository;
import com.ojasvi.ecommerce.Repository.ProductRepository;

import java.math.BigDecimal;
import com.ojasvi.ecommerce.Enum.OrderStatus;
import com.ojasvi.ecommerce.Enum.PaymentMethod;
import com.ojasvi.ecommerce.Enum.PaymentStatus;

@Service
public class CheckoutService {

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

	@Transactional
	public Order placeOrder(User user, Address shippingAddress, String paymentMethod) {

		Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Cart not found"));

		List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

		if (cartItems.isEmpty()) {
			throw new RuntimeException("Cart is empty");
		}

		Order order = new Order();

		order.setOrderNumber("ORD-" + System.currentTimeMillis());
		order.setCustomer(user);

		order.setShippingAddress(shippingAddress);

		order.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));

		order.setOrderStatus(OrderStatus.PENDING);
		order.setPaymentStatus(PaymentStatus.PENDING);

		BigDecimal subtotal = BigDecimal.ZERO;

		order = orderRepository.save(order);

		for (CartItem item : cartItems) {

			Product product = item.getProduct();

			if (product.getStock() < item.getQuantity()) {
				throw new RuntimeException(product.getProductName() + " is out of stock");
			}

			BigDecimal price = product.getSellingPrice();

			BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

			OrderItem orderItem = new OrderItem();

			orderItem.setOrder(order);
			orderItem.setProduct(product);
			orderItem.setProductName(product.getProductName());
			orderItem.setProductPrice(price);
			orderItem.setQuantity(item.getQuantity());
			orderItem.setSubtotal(lineTotal);

			orderItemRepository.save(orderItem);

			subtotal = subtotal.add(lineTotal);

			product.setStock(product.getStock() - item.getQuantity());

			productRepository.save(product);
		}

		BigDecimal shippingCharge = BigDecimal.ZERO;
		BigDecimal discountAmount = BigDecimal.ZERO;
		BigDecimal taxAmount = BigDecimal.ZERO;

		order.setSubtotal(subtotal);
		order.setShippingCharge(shippingCharge);
		order.setDiscountAmount(discountAmount);
		order.setTaxAmount(taxAmount);

		BigDecimal grandTotal = subtotal.add(shippingCharge).add(taxAmount).subtract(discountAmount);

		order.setGrandTotal(grandTotal);

		orderRepository.save(order);

		cartItemRepository.deleteByCartId(cart.getId());

		cart.setTotalAmount(BigDecimal.ZERO);
		cart.setTotalItems(0);

		cartRepository.save(cart);

		return order;
	}
}
