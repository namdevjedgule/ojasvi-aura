package com.ojasvi.ecommerce.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ojasvi.ecommerce.Entity.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/shipping")
public class AdminShippingController {

    @GetMapping
    public String shipping(HttpSession session, Model model) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", admin);

        return "admin/shipping";
    }
}
