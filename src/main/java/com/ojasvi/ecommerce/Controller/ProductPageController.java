package com.ojasvi.ecommerce.Controller;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;                          // FIX: was ch.qos.logback.core.model.Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
 
import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.SessionUtil;
import com.ojasvi.ecommerce.Service.CategoryService;
import com.ojasvi.ecommerce.Service.ProductService;
import com.ojasvi.ecommerce.Util.WishlistHelper;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProductPageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WishlistHelper wishlistHelper;

    @Autowired
    private HttpSession session;

    @GetMapping("/shop")
    public String shop(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            Model model) {

        var products = productService.getShopProducts();
        var categories = categoryService.getCategoriesForShop();

        User user = SessionUtil.getLoggedInUser(session);

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("totalProductCount", products.size());

        model.addAttribute("wishlistIds",
                wishlistHelper.getWishlistIds(user));

        return "shop";
    }

    @GetMapping("/collections")
    public String collections(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "collections";
    }

    @GetMapping("/product/{slug}")
    public String productDetails(@PathVariable String slug, Model model) {

        Product product = productService.getBySlugWithImages(slug);

        if (product == null) return "redirect:/shop";

        User user = SessionUtil.getLoggedInUser(session);

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", productService.getRelated(product));

        model.addAttribute("wishlistIds",
                wishlistHelper.getWishlistIds(user));

        return "product-details";
    }
}
