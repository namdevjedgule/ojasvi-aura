package com.ojasvi.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.AccessValidator;
import com.ojasvi.ecommerce.Security.SessionUtil;
import com.ojasvi.ecommerce.Service.CategoryService;
import com.ojasvi.ecommerce.Service.OrderService;
import com.ojasvi.ecommerce.Service.ProductService;
import com.ojasvi.ecommerce.Service.SubCategoryService;
import com.ojasvi.ecommerce.Service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {
	
	@Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private SubCategoryService subCategoryService;

	@GetMapping("/customer-dashboard")
	public String customerDashboard(
	        HttpSession session,
	        Model model) {

		User user = SessionUtil.getLoggedInUser(session);

		if (user == null) {
		    return "redirect:/login";
		}

		if (!AccessValidator.isCustomer(user)) {
		    return "redirect:/admin-dashboard";
		}

	    model.addAttribute("user", user);

	    return "customer-dashboard";
	}

	@GetMapping("/admin-dashboard")
	public String adminDashboard(
	        HttpSession session,
	        Model model) {

		User user = SessionUtil.getLoggedInUser(session);

		if (user == null) {
		    return "redirect:/login";
		}

		if (!AccessValidator.isAdmin(user)) {
		    return "redirect:/customer-dashboard";
		}

	    model.addAttribute("user", user);
	    
	    model.addAttribute("totalProducts",
	            productService.countProducts());

	    model.addAttribute("totalOrders",
	            orderService.countOrders());

	    model.addAttribute("totalCustomers",
	            userService.countCustomers());

	    model.addAttribute("lowStockCount",
	            productService.lowStockCount());
	    
	    model.addAttribute("products", productService.findAll());
	    model.addAttribute("customers", userService.findAllCustomers());
	    model.addAttribute("recentOrders", orderService.findRecentOrders());
	    model.addAttribute("categories", categoryService.getAllCategories());
	    model.addAttribute("subCategories", subCategoryService.findAll());

	    return "admin-dashboard";
	}
}