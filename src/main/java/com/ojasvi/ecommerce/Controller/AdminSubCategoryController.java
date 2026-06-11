package com.ojasvi.ecommerce.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Service.CategoryService;
import com.ojasvi.ecommerce.Service.SubCategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/subcategories")
public class AdminSubCategoryController {
	
	@Autowired
	private SubCategoryService subCategoryService;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping
    public String categoryList( HttpSession session,
	        Model model) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
        model.addAttribute("categories", categoryService.getAllCategories());

        return "admin/sub-category";
    }

}
