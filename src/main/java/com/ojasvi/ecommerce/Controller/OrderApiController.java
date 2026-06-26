package com.ojasvi.ecommerce.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Service.OrderService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

	@Autowired
	private OrderService orderService;

	@PostMapping("/cancel/{id}")
	public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable Long id, HttpSession session) {

		User user = (User) session.getAttribute("user");

		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return ResponseEntity.ok(orderService.cancelOrder(id, user));
	}

	@PostMapping("/reorder/{id}")
	public ResponseEntity<Map<String, Object>> reorder(@PathVariable Long id, HttpSession session) {

		User user = (User) session.getAttribute("user");

		if (user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		return ResponseEntity.ok(orderService.reorder(id, user));
	}
	
	@GetMapping("/{id}/tracking")
	public ResponseEntity<?> getTracking(@PathVariable Long id,
	                                     HttpSession session) {

	    User user = (User) session.getAttribute("user");

	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    return ResponseEntity.ok(orderService.getTracking(id, user));
	}
	
	@GetMapping("/{id}/timeline")
	public ResponseEntity<?> getTimeline(@PathVariable Long id,
	                                     HttpSession session) {

	    User user = (User) session.getAttribute("user");

	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }

	    return ResponseEntity.ok(orderService.getTimeline(id, user));
	}

}
