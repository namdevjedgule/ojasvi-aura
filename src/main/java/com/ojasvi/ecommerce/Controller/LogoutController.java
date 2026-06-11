package com.ojasvi.ecommerce.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LogoutController {

	@PostMapping("/logout")
	public String logout(HttpServletRequest request) {

	    HttpSession session = request.getSession(false);

	    if (session != null) {
	        session.invalidate();
	    }

	    String referer = request.getHeader("Referer");

	    if (referer != null &&
	        (referer.contains("dashboard") || referer.contains("admin"))) {
	        return "redirect:/login?logout=true";
	    }

	    if (referer != null) {
	        return "redirect:" + referer;
	    }

	    return "redirect:/";
	}
}
