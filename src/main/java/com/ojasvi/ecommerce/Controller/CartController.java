package com.ojasvi.ecommerce.Controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.ojasvi.ecommerce.DTO.CartItemDTO;
import com.ojasvi.ecommerce.Entity.Cart;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.SessionUtil;
import com.ojasvi.ecommerce.Service.CartService;

import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {

	@Autowired
	private CartService cartService;

	@GetMapping("/cart")
	public String cart(HttpSession session, Model model) {

	    User user = SessionUtil.getLoggedInUser(session);

	    if (user == null) {
	        return "redirect:/login";
	    }

	    Cart cart = cartService.getCartByUser(user);

	    List<CartItemDTO> cartItems = cartService.getCartItems(user);

	    model.addAttribute("cart", cart);
	    model.addAttribute("cartItems", cartItems);

	    return "cart";
	}
}
