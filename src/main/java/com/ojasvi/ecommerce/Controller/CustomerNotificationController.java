package com.ojasvi.ecommerce.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ojasvi.ecommerce.Entity.Notification;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Enum.NotificationType;
import com.ojasvi.ecommerce.Enum.RecipientType;
import com.ojasvi.ecommerce.Service.NotificationService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/notifications")
public class CustomerNotificationController {

	@Autowired
	private NotificationService notificationService;

	@GetMapping
	public String notificationPage(@RequestParam(defaultValue = "0") int page, HttpSession session, Model model) {

		User user = (User) session.getAttribute("user");

		// Not logged in
		if (user == null) {
			return "redirect:/login";
		}

		// Prevent Admin from opening customer pages
		if (!"CUSTOMER".equalsIgnoreCase(user.getRole().getRoleName())) {
			return "redirect:/access-denied";
		}

		Pageable pageable = PageRequest.of(page, 10);

		Page<Notification> notificationPage = notificationService.getNotifications(user.getId(), RecipientType.CUSTOMER,
				pageable);

		model.addAttribute("user", user);
		model.addAttribute("activePage", "notifications");
		model.addAttribute("notifications", notificationPage.getContent());
		model.addAttribute("currentPage", notificationPage.getNumber());
		model.addAttribute("totalPages", notificationPage.getTotalPages());
		model.addAttribute("totalNotifications", notificationPage.getTotalElements());

		model.addAttribute("unreadCount", notificationService.getUnreadCount(user.getId(), RecipientType.CUSTOMER));

		model.addAttribute("orderNotifCount",
				notificationService.getCount(user.getId(), RecipientType.CUSTOMER, NotificationType.ORDER));

		model.addAttribute("paymentNotifCount",
				notificationService.getCount(user.getId(), RecipientType.CUSTOMER, NotificationType.PAYMENT));

		model.addAttribute("stockNotifCount",
				notificationService.getCount(user.getId(), RecipientType.CUSTOMER, NotificationType.STOCK));

		model.addAttribute("supportNotifCount",
				notificationService.getCount(user.getId(), RecipientType.CUSTOMER, NotificationType.SUPPORT));

		return "customer/customer-notifications";
	}

	@PostMapping("/mark-read/{id}")
	@ResponseBody
	public Map<String, Object> markRead(@PathVariable Long id, HttpSession session) {

		User user = (User) session.getAttribute("user");

		if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole().getRoleName())) {

			return Map.of("success", false, "message", "Unauthorized");
		}

		notificationService.markAsRead(id, user.getId(), RecipientType.CUSTOMER);

		return Map.of("success", true, "message", "Notification marked as read");
	}

	@PostMapping("/mark-all-read")
	@ResponseBody
	public Map<String, Object> markAllRead(HttpSession session) {

		User user = (User) session.getAttribute("user");

		if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole().getRoleName())) {

			return Map.of("success", false, "message", "Unauthorized");
		}

		notificationService.markAllAsRead(user.getId(), RecipientType.CUSTOMER);

		return Map.of("success", true, "message", "All notifications marked as read");
	}

	@PostMapping("/delete/{id}")
	@ResponseBody
	public Map<String, Object> deleteNotification(@PathVariable Long id, HttpSession session) {

		User user = (User) session.getAttribute("user");

		if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole().getRoleName())) {

			return Map.of("success", false, "message", "Unauthorized");
		}

		notificationService.deleteNotification(id, user.getId(), RecipientType.CUSTOMER);

		return Map.of("success", true, "message", "Notification deleted");
	}

	@PostMapping("/clear-all")
	@ResponseBody
	public Map<String, Object> clearAllNotifications(HttpSession session) {

		User user = (User) session.getAttribute("user");

		if (user == null || !"CUSTOMER".equalsIgnoreCase(user.getRole().getRoleName())) {

			return Map.of("success", false, "message", "Unauthorized");
		}

		notificationService.clearAllNotifications(user.getId(), RecipientType.CUSTOMER);

		return Map.of("success", true, "message", "All notifications cleared");
	}

}
