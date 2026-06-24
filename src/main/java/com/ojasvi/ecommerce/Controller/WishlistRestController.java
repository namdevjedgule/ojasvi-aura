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
import com.ojasvi.ecommerce.Service.WishlistService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistRestController {
	
	@Autowired private WishlistService wishlistService;

    @PostMapping("/toggle")
    public ResponseEntity<?> toggle(
            @RequestBody Map<String, Object> payload,
            HttpSession session) {

        User user = SessionUtil.getLoggedInUser(session);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Please login first"));
        }

        Long productId = Long.valueOf(payload.get("productId").toString());

        return ResponseEntity.ok(
                wishlistService.toggleWishlist(user, productId)
        );
    }
}
