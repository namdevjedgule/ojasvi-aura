package com.ojasvi.ecommerce.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.SessionUtil;
import com.ojasvi.ecommerce.Service.CartService;
import com.ojasvi.ecommerce.Service.WishlistService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/navbar")
public class NavbarRestController {

    @Autowired
    private CartService cartService;

    @Autowired
    private WishlistService wishlistService;

    @GetMapping("/counts")
    public Map<String, Integer> getCounts(HttpSession session) {

        User user = SessionUtil.getLoggedInUser(session);

        if (user == null) {
            return Map.of(
                    "wishlistCount", 0,
                    "cartCount", 0
            );
        }

        return Map.of(
                "wishlistCount", wishlistService.getWishlistCount(user),
                "cartCount", cartService.getCartItemCount(user)
        );
    }
}
