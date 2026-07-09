package com.ojasvi.ecommerce.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.ojasvi.ecommerce.Entity.User;

import jakarta.servlet.http.HttpSession;

@Controller
public class ErrorPageController {

	@GetMapping("/access-denied")
	public String accessDenied(HttpSession session) {

	    User user = (User) session.getAttribute("user");

	    if (user != null) {
	        if ("ADMIN".equalsIgnoreCase(user.getRole().getRoleName())) {
	            return "redirect:/admin-dashboard";
	        }

	        if ("CUSTOMER".equalsIgnoreCase(user.getRole().getRoleName())) {
	            return "redirect:/customer-dashboard";
	        }
	    }

	    return "errors/access-denied";
	}

}
