package com.ojasvi.ecommerce.Controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ojasvi.ecommerce.Entity.Category;
import com.ojasvi.ecommerce.Entity.Coupon;
import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Enum.DiscountType;
import com.ojasvi.ecommerce.Repository.CategoryRepository;
import com.ojasvi.ecommerce.Repository.ProductRepository;
import com.ojasvi.ecommerce.Service.CategoryService;
import com.ojasvi.ecommerce.Service.CouponService;
import com.ojasvi.ecommerce.Service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/coupons")
public class AdminCouponController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CouponService couponService;

	@GetMapping
	public String couponPage(@RequestParam(required = false) String success, HttpSession session, Model model) {

		User admin = (User) session.getAttribute("user");

		if (admin == null) {
			return "redirect:/login";
		}

		model.addAttribute("user", admin);

		model.addAttribute("categories", categoryService.getAllCategories());

		model.addAttribute("products", productService.getAllProducts());

		model.addAttribute("coupons", couponService.getAllCoupons());

		model.addAttribute("totalCoupons", couponService.getTotalCoupons());

		model.addAttribute("activeCoupons", couponService.getActiveCoupons());

		model.addAttribute("totalUses", couponService.getTotalUses());

		model.addAttribute("totalSavings", couponService.getTotalSavings());

		if (success != null) {
			model.addAttribute("success", "Coupon saved successfully.");
		}

		return "admin/coupons";
	}

	@PostMapping("/save")
	public String saveCoupon(@ModelAttribute Coupon coupon, RedirectAttributes redirectAttributes) {

		try {

			if (coupon.getCategory() != null && coupon.getCategory().getId() == null) {
				coupon.setCategory(null);
			}

			if (coupon.getProduct() != null && coupon.getProduct().getId() == null) {
				coupon.setProduct(null);
			}

			if (coupon.getApplyType() != null) {

				switch (coupon.getApplyType()) {

				case ALL:

					coupon.setCategory(null);
					coupon.setProduct(null);
					break;

				case CATEGORY:

					coupon.setProduct(null);

					if (coupon.getCategory() == null || coupon.getCategory().getId() == null) {

						throw new RuntimeException("Please select a category");
					}

					Category category = categoryRepository.findById(coupon.getCategory().getId())
							.orElseThrow(() -> new RuntimeException("Category not found"));

					coupon.setCategory(category);

					break;

				case PRODUCT:

					coupon.setCategory(null);

					if (coupon.getProduct() == null || coupon.getProduct().getId() == null) {

						throw new RuntimeException("Please select a product");
					}

					Product product = productRepository.findById(coupon.getProduct().getId())
							.orElseThrow(() -> new RuntimeException("Product not found"));

					coupon.setProduct(product);

					break;
				}
			}

			if (coupon.getUsedCount() == null) {
				coupon.setUsedCount(0);
			}

			if (coupon.getIsActive() == null) {
				coupon.setIsActive(true);
			}

			if (coupon.getDeleted() == null) {
				coupon.setDeleted(false);
			}

			if (coupon.getPriority() == null) {
				coupon.setPriority(1);
			}

			if (coupon.getFreeShipping() == null) {
				coupon.setFreeShipping(false);
			}

			if (coupon.getAutoApply() == null) {
				coupon.setAutoApply(false);
			}

			if (coupon.getFirstOrderOnly() == null) {
				coupon.setFirstOrderOnly(false);
			}

			if (coupon.getCode() != null) {
				coupon.setCode(coupon.getCode().trim().toUpperCase());
			}
			
			if (coupon.getTitle() != null) {
			    coupon.setTitle(
			        coupon.getTitle().trim());
			}
			
			if (coupon.getStartDate() != null &&
				    coupon.getExpiryDate() != null &&
				    coupon.getStartDate().isAfter(
				            coupon.getExpiryDate())) {

				    throw new RuntimeException(
				            "Expiry date must be after start date");
				}
			
			if (coupon.getDiscountType() ==
			        DiscountType.PERCENT &&
			    coupon.getDiscountValue()
			        .compareTo(new BigDecimal("100")) > 0) {

			    throw new RuntimeException(
			            "Percentage discount cannot exceed 100%");
			}
			
			Coupon existing =
			        couponService.findByCode(
			                coupon.getCode());

			if (existing != null &&
			    !existing.getId().equals(
			            coupon.getId())) {

			    throw new RuntimeException(
			            "Coupon code already exists");
			}

			couponService.save(coupon);

			redirectAttributes.addFlashAttribute("success", "Coupon saved successfully.");

		} catch (Exception e) {

			e.printStackTrace();

			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/admin/coupons";
	}

	@PostMapping("/delete/{id}")
	public String deleteCoupon(@PathVariable Long id) {

		couponService.delete(id);

		return "redirect:/admin/coupons";
	}
}
