package com.ojasvi.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/inventory")
public class AdminInventoryController {
	
	@GetMapping
    public String inventoryList( HttpSession session,
	        Model model) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", admin);

        return "admin/inventory";
    }

}
