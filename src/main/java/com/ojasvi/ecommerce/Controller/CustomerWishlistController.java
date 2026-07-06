package com.ojasvi.ecommerce.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ojasvi.ecommerce.DTO.WishlistDTO;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.AccessValidator;
import com.ojasvi.ecommerce.Service.WishlistService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/wishlist")
public class CustomerWishlistController {
	
	@Autowired
	private WishlistService wishlistService;

	@GetMapping
    public String customerWishlist(HttpSession session,
                                 Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!AccessValidator.isCustomer(user)) {
            return "redirect:/admin-dashboard";
        }
        
        List<WishlistDTO> wishlist = wishlistService.getWishlist(user);

	    model.addAttribute("wishlistItems", wishlist);

        model.addAttribute("user", user);

        return "customer/customer-wishlist";
    }
}
