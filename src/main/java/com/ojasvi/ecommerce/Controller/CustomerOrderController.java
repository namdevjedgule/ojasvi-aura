package com.ojasvi.ecommerce.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.ojasvi.ecommerce.DTO.OrderTrackingDTO;
import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Enum.OrderStatus;
import com.ojasvi.ecommerce.Security.AccessValidator;
import com.ojasvi.ecommerce.Service.InvoiceService;
import com.ojasvi.ecommerce.Service.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/orders")
public class CustomerOrderController {
	
	 @Autowired
	 private OrderService orderService;
	 
	 @Autowired
	 private InvoiceService invoiceService;

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
    
    @GetMapping("/invoice/{orderId}")
    public ResponseEntity<InputStreamResource> downloadInvoice(
            @PathVariable Long orderId,
            HttpSession session)
            throws IOException {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Order order = orderService.findByIdWithItems(orderId);

        // Security check
        if (!order.getCustomer().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().build();
        }

        // Allow invoice only after delivery
        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            return ResponseEntity.badRequest().build();
        }

        ByteArrayInputStream pdf =
                invoiceService.generateInvoice(order);

        HttpHeaders headers = new HttpHeaders();

        String fileName =
                "Invoice-" +
                order.getInvoiceNumber() +
                ".pdf";

        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }
}
