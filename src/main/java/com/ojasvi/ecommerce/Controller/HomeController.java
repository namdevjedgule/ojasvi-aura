package com.ojasvi.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ojasvi.ecommerce.Service.ProductService;
import com.ojasvi.ecommerce.Service.SubCategoryService;

@Controller
public class HomeController {

	@Autowired
	private SubCategoryService subCategoryService;

	@Autowired
	private ProductService productService;

	@GetMapping("/")
	public String home(Model model) {

		model.addAttribute("subCategories", subCategoryService.findAllActive());

		model.addAttribute("bestSellers", productService.getBestSellerProducts());

		model.addAttribute("featuredProducts", productService.getFeaturedTopTwelveProducts());

		model.addAttribute("newArrivals", productService.getNewArrivalProducts());

		return "index";
	}

	@GetMapping("/about")
	public String about() {
		return "about";
	}

	@GetMapping("/contact")
	public String contact() {
		return "contact";
	}
}
