package com.ojasvi.ecommerce.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ojasvi.ecommerce.DTO.WishlistDTO;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.SessionUtil;
import com.ojasvi.ecommerce.Service.WishlistService;

import jakarta.servlet.http.HttpSession;

@Controller
public class WishlistController {
	
	@Autowired
	private WishlistService wishlistService;

	@GetMapping("/wishlist")
	public String wishlist(HttpSession session, Model model) {

	    User user = SessionUtil.getLoggedInUser(session);

	    if (user == null) {
	        return "redirect:/login";
	    }

	    List<WishlistDTO> wishlist = wishlistService.getWishlist(user);

	    model.addAttribute("wishlistItems", wishlist);

	    return "wishlist";
	}
}
