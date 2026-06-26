package com.ojasvi.ecommerce.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ojasvi.ecommerce.DTO.OrderTrackingDTO;
import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.AccessValidator;
import com.ojasvi.ecommerce.Service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/orders")
public class CustomerOrderController {
	
	 @Autowired
	 private OrderService orderService;

    @GetMapping
    public String customerOrders(HttpSession session,
                                 Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!AccessValidator.isCustomer(user)) {
            return "redirect:/admin-dashboard";
        }
        
        List<Order> orders = orderService.getCustomerOrders(user);

        Map<Long, OrderTrackingDTO> trackingMap = new HashMap<>();

        for (Order order : orders) {
            trackingMap.put(order.getId(), orderService.getTrackingTimeline(order));
        }

        model.addAttribute("orders", orders);
        model.addAttribute("trackingMap", trackingMap);
        model.addAttribute("user", user);model.addAttribute("user", user);

        return "customer/customer-orders";
    }
}
