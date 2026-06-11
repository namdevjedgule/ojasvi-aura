package com.ojasvi.ecommerce.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.AccessValidator;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/loyalty")
public class CustomerLoyaltyController {
	
	@GetMapping
    public String customerLoyalty(HttpSession session,
                                 Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!AccessValidator.isCustomer(user)) {
            return "redirect:/admin-dashboard";
        }

        model.addAttribute("user", user);

        return "customer/customer-loyalty";
    }
}
