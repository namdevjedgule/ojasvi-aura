package com.ojasvi.ecommerce.Advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Enum.RecipientType;
import com.ojasvi.ecommerce.Service.CartService;
import com.ojasvi.ecommerce.Service.NotificationService;
import com.ojasvi.ecommerce.Service.WishlistService;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private CartService cartService;

    @Autowired
    private WishlistService wishlistService;
    
    @Autowired
    private NotificationService notificationService;

    @ModelAttribute
    public void addAttributes(HttpSession session,
                              Model model) {

        User user = (User) session.getAttribute("user");

        model.addAttribute("user", user);

        if (user != null) {

            model.addAttribute("cartCount",
                    cartService.getCartItemCount(user));

            model.addAttribute("wishlistCount",
                    wishlistService.getWishlistCount(user));
            
            RecipientType recipientType = user.getRole().getRoleName().equalsIgnoreCase("Admin")
                    ? RecipientType.ADMIN
                    : RecipientType.CUSTOMER;

            model.addAttribute("notificationCount",
                    notificationService.getUnreadCount(user.getId(), recipientType));

        } else {

            model.addAttribute("cartCount", 0);
            model.addAttribute("wishlistCount", 0);
            model.addAttribute("notificationCount", 0);

        }
    }
}
