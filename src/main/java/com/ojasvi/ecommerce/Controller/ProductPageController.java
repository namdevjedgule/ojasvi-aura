package com.ojasvi.ecommerce.Controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;                          // FIX: was ch.qos.logback.core.model.Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
 
import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Service.CategoryService;
import com.ojasvi.ecommerce.Service.ProductService;

@Controller
public class ProductPageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/shop")
    public String shop(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            Model model) {

        model.addAttribute("products",   productService.getActiveProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "shop";
    }

    @GetMapping("/collections")
    public String collections(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "collections";
    }

    @GetMapping("/product/{slug}")   
    public String productDetails(@PathVariable String slug, Model model) {

        Product product = productService.getBySlug(slug);

        if (product == null) return "redirect:/shop";

        model.addAttribute("product",         product);
        model.addAttribute("relatedProducts", productService.getRelated(product));
        return "product-details";
    }
}
