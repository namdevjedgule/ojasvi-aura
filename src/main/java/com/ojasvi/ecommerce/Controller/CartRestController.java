package com.ojasvi.ecommerce.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.SessionUtil;
import com.ojasvi.ecommerce.Service.CartService;
import com.ojasvi.ecommerce.Service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/cart")
public class CartRestController {

	@Autowired
	private CartService cartService;
	
    @Autowired 
    private UserService userService;

	 @PostMapping("/add")
	    public ResponseEntity<?> addToCart(
	            @RequestBody Map<String, Object> payload,
	            HttpSession session) {

	        User user = SessionUtil.getLoggedInUser(session);

	        if (user == null) {
	            return ResponseEntity.status(401)
	                    .body(Map.of("message", "Please login first"));
	        }

	        Long productId = Long.valueOf(payload.get("productId").toString());
	        Integer qty = Integer.valueOf(payload.get("quantity").toString());

	        return ResponseEntity.ok(
	                cartService.addToCart(user, productId, qty)
	        );
	    }
	 
	 @PostMapping("/remove")
	 public ResponseEntity<?> removeFromCart(
	         @RequestBody Map<String, Object> payload,
	         HttpSession session) {

	     User user = SessionUtil.getLoggedInUser(session);

	     if (user == null) {
	         return ResponseEntity.status(401)
	                 .body(Map.of(
	                         "success", false,
	                         "message", "Please login first"
	                 ));
	     }

	     Long productId =
	             Long.valueOf(payload.get("productId").toString());

	     cartService.removeFromCart(user, productId);

	     return ResponseEntity.ok(
	             Map.of(
	                     "success", true,
	                     "message", "Item removed successfully"
	             )
	     );
	 }
}
