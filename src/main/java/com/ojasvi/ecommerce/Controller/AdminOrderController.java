package com.ojasvi.ecommerce.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ojasvi.ecommerce.DTO.OrderDetailsDto;
import com.ojasvi.ecommerce.DTO.StatusRequest;
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
		model.addAttribute("totalOrders", orderService.countOrders());
		model.addAttribute("pendingOrders", orderService.countPendingOrders());
		model.addAttribute("confirmedOrders", orderService.countConfirmedOrders());
		model.addAttribute("packedOrders", orderService.countPackedOrders());
		model.addAttribute("shippedOrders", orderService.countShippedOrders());
		model.addAttribute("deliveredOrders", orderService.countDeliveredOrders());
		model.addAttribute("cancelledOrders", orderService.countCancelledOrders());

		return "admin/order";
	}

	@GetMapping("/data/{id}")
	@ResponseBody
	public OrderDetailsDto orderDetails(@PathVariable Long id) {

		return orderService.getOrderDetails(id);

	}

	@PostMapping("/update-status/{id}")
	@ResponseBody
	public Map<String, Object> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {

		Map<String, Object> map = new HashMap<>();

		try {

			orderService.updateOrderStatus(id, request.getStatus());

			map.put("success", true);

			map.put("message", "Updated Successfully");

		}

		catch (Exception e) {

			map.put("success", false);

			map.put("message", e.getMessage());

		}

		return map;

	}

}
