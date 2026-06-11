package com.ojasvi.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping
    public String orderList(HttpSession session, Model model) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", admin);

        model.addAttribute("orders", orderService.getAllOrders());

        model.addAttribute("totalOrders",
                orderService.getAllOrders().size());

        return "admin/order";
    }
}
