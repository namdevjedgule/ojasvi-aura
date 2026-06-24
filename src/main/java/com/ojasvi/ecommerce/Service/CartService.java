package com.ojasvi.ecommerce.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ojasvi.ecommerce.DTO.CartItemDTO;
import com.ojasvi.ecommerce.Entity.Cart;
import com.ojasvi.ecommerce.Entity.CartItem;
import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Repository.CartItemRepository;
import com.ojasvi.ecommerce.Repository.CartRepository;
import com.ojasvi.ecommerce.Repository.ProductRepository;

@Service
public class CartService {

	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	public Cart getCartByUser(User user) {

	    return cartRepository.findCartWithItems(user.getId())
	            .orElseGet(() -> {
	                Cart cart = new Cart();
	                cart.setUser(user);
	                cart.setTotalItems(0);
	                cart.setTotalAmount(BigDecimal.ZERO);
	                return cartRepository.save(cart);
	            });
	}
	
	@Transactional(readOnly = true)
	public List<CartItemDTO> getCartItems(User user) {

	    Cart cart = getOrCreateCart(user);

	    List<CartItem> items = cartItemRepository.findCartItemsWithProduct(cart.getId());

	    return items.stream()
	            .map(item -> new CartItemDTO(
	                    item.getId(),
	                    item.getProduct().getId(),
	                    item.getProduct().getProductName(),
	                    item.getProduct().getImages().isEmpty()
	                            ? null
	                            : item.getProduct().getImages().get(0).getImageUrl(),
	                    item.getQuantity(),
	                    item.getPrice(),
	                    item.getSubtotal()
	            ))
	            .toList();
	}

	public Cart getOrCreateCart(User user) {

		return cartRepository.findByUserId(user.getId()).orElseGet(() -> {
			Cart cart = new Cart();
			cart.setUser(user);
			cart.setTotalAmount(BigDecimal.ZERO);
			cart.setTotalItems(0);
			return cartRepository.save(cart);
		});
	}

	public Map<String, Object> addToCart(User user, Long productId, Integer qty) {

	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new RuntimeException("Product not found"));

	    Cart cart = getOrCreateCart(user);

	    CartItem item = cartItemRepository
	            .findByCartIdAndProductId(cart.getId(), productId)
	            .orElse(null);

	    if (item == null) {

	        item = new CartItem();
	        item.setCart(cart);
	        item.setProduct(product);
	        item.setQuantity(qty);
	        item.setPrice(product.getSellingPrice());

	    } else {

	        item.setQuantity(item.getQuantity() + qty);
	    }

	    item.setSubtotal(
	            item.getPrice()
	                .multiply(BigDecimal.valueOf(item.getQuantity()))
	    );

	    cartItemRepository.save(item);

	    updateCartSummary(cart);

	    return Map.of(
	            "success", true,
	            "message", "Added to cart"
	    );
	}

	private void updateCartSummary(Cart cart) {

	    List<CartItem> items =
	            cartItemRepository.findByCartId(cart.getId());

	    int totalItems = items.stream()
	            .mapToInt(CartItem::getQuantity)
	            .sum();

	    BigDecimal totalAmount = items.stream()
	            .map(CartItem::getSubtotal)
	            .reduce(BigDecimal.ZERO, BigDecimal::add);

	    cart.setTotalItems(totalItems);
	    cart.setTotalAmount(totalAmount);

	    cartRepository.save(cart);
	}

	@Transactional
	public void removeFromCart(User user, Long productId) {

	    Cart cart = cartRepository.findByUser(user)
	            .orElseThrow(() ->
	                    new RuntimeException("Cart not found"));

	    CartItem cartItem = cartItemRepository
	            .findByCartAndProductId(cart, productId)
	            .orElseThrow(() ->
	                    new RuntimeException("Item not found in cart"));

	    cart.setTotalAmount(
	            cart.getTotalAmount()
	                    .subtract(cartItem.getSubtotal())
	    );

	    cart.setTotalItems(
	            cart.getTotalItems() - cartItem.getQuantity()
	    );

	    cartItemRepository.delete(cartItem);

	    cartRepository.save(cart);
	}
	
	public Cart getCartByUserId(Long userId) {

	    return cartRepository.findByUserId(userId)
	            .orElseThrow(() ->
	                    new RuntimeException("Cart not found"));
	}
}
