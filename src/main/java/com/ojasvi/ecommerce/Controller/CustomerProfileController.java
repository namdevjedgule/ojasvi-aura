package com.ojasvi.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.AccessValidator;
import com.ojasvi.ecommerce.Service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer/profile")
public class CustomerProfileController {
	
	@Autowired
    private UserService userService;
	
	 @GetMapping
	    public String customerProfile(HttpSession session,
	                                 Model model) {

	        User user = (User) session.getAttribute("user");

	        if (user == null) {
	            return "redirect:/login";
	        }

	        if (!AccessValidator.isCustomer(user)) {
	            return "redirect:/admin-dashboard";
	        }

	        model.addAttribute("user", user);

	        return "customer/customer-profile";
	    }
	 
	 @PostMapping("/update")
	    public String updateProfile(
	            @RequestParam String fullName,
	            @RequestParam String email,
	            @RequestParam(required = false) String mobile,
	            HttpSession session) {

	        User sessionUser = (User) session.getAttribute("user");

	        if (sessionUser == null) {
	            return "redirect:/login";
	        }

	        userService.updateProfile(
	                sessionUser.getId(),
	                fullName,
	                email,
	                mobile);

	        User updatedUser =
	                userService.findById(sessionUser.getId());

	        session.setAttribute("user", updatedUser);

	        return "redirect:/customer/profile?success";
	    }

	    @PostMapping("/change-password")
	    public String changePassword(
	            @RequestParam String currentPassword,
	            @RequestParam String newPassword,
	            HttpSession session) {

	        User sessionUser = (User) session.getAttribute("user");

	        if (sessionUser == null) {
	            return "redirect:/login";
	        }

	        userService.changePassword(
	                sessionUser.getId(),
	                currentPassword,
	                newPassword);

	        return "redirect:/customer/profile?passwordChanged";
	    }

	    @PostMapping("/upload-image")
	    public String uploadProfileImage(
	            @RequestParam("image") MultipartFile image,
	            HttpSession session) {

	        User sessionUser = (User) session.getAttribute("user");

	        if (sessionUser == null) {
	            return "redirect:/login";
	        }

	        userService.uploadProfileImage(
	                sessionUser.getId(),
	                image);

	        User updatedUser =
	                userService.findById(sessionUser.getId());

	        session.setAttribute("user", updatedUser);

	        return "redirect:/customer/profile?imageUpdated";
	    }

	    @PostMapping("/remove-image")
	    public String removeProfileImage(HttpSession session) {

	        User sessionUser = (User) session.getAttribute("user");

	        if (sessionUser == null) {
	            return "redirect:/login";
	        }

	        userService.removeProfileImage(
	                sessionUser.getId());

	        User updatedUser =
	                userService.findById(sessionUser.getId());

	        session.setAttribute("user", updatedUser);

	        return "redirect:/customer/profile?imageRemoved";
	    }
}
