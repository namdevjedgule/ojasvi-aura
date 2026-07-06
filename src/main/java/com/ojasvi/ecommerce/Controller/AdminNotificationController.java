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
@RequestMapping("/admin/notifications")
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public String notificationPage(
            @RequestParam(defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, 10);

        Page<Notification> notificationPage =
                notificationService.getNotifications(
                        admin.getId(),
                        RecipientType.ADMIN,
                        pageable);

        model.addAttribute("user", admin);
        
        model.addAttribute("activePage", "notifications");

        model.addAttribute("notifications",
                notificationPage.getContent());

        model.addAttribute("currentPage",
                notificationPage.getNumber());

        model.addAttribute("totalPages",
                notificationPage.getTotalPages());

        model.addAttribute("totalNotifications",
                notificationPage.getTotalElements());

        model.addAttribute("unreadCount",
                notificationService.getUnreadCount(
                        admin.getId(),
                        RecipientType.ADMIN));

        model.addAttribute("orderNotifCount",
                notificationService.getCount(
                        admin.getId(),
                        RecipientType.ADMIN,
                        NotificationType.ORDER));

        model.addAttribute("paymentNotifCount",
                notificationService.getCount(
                        admin.getId(),
                        RecipientType.ADMIN,
                        NotificationType.PAYMENT));

        model.addAttribute("stockNotifCount",
                notificationService.getCount(
                        admin.getId(),
                        RecipientType.ADMIN,
                        NotificationType.STOCK));

        model.addAttribute("supportNotifCount",
                notificationService.getCount(
                        admin.getId(),
                        RecipientType.ADMIN,
                        NotificationType.SUPPORT));

        return "admin/admin-notifications";
    }

    @PostMapping("/mark-read/{id}")
    @ResponseBody
    public Map<String, Object> markRead(
            @PathVariable Long id,
            HttpSession session) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return Map.of(
                    "success", false,
                    "message", "Session expired");
        }

        notificationService.markAsRead(
                id,
                admin.getId(),
                RecipientType.ADMIN);

        return Map.of(
                "success", true,
                "message", "Notification marked as read");
    }

    @PostMapping("/mark-all-read")
    @ResponseBody
    public Map<String, Object> markAllRead(HttpSession session) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return Map.of(
                    "success", false,
                    "message", "Session expired");
        }

        notificationService.markAllAsRead(
                admin.getId(),
                RecipientType.ADMIN);

        return Map.of(
                "success", true,
                "message", "All notifications marked as read");
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteNotification(
            @PathVariable Long id,
            HttpSession session) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return Map.of(
                    "success", false,
                    "message", "Session expired");
        }

        notificationService.deleteNotification(
                id,
                admin.getId(),
                RecipientType.ADMIN);

        return Map.of(
                "success", true,
                "message", "Notification deleted");
    }

    @PostMapping("/clear-all")
    @ResponseBody
    public Map<String, Object> clearAllNotifications(
            HttpSession session) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return Map.of(
                    "success", false,
                    "message", "Session expired");
        }

        notificationService.clearAllNotifications(
                admin.getId(),
                RecipientType.ADMIN);

        return Map.of(
                "success", true,
                "message", "All notifications cleared");
    }

}