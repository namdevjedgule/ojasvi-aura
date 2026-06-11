package com.ojasvi.ecommerce.Controller;
 
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;                                    
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.SubCategory;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Service.CategoryService;
import com.ojasvi.ecommerce.Service.ProductService;
import com.ojasvi.ecommerce.Service.SubCategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubCategoryService subCategoryService;

    @GetMapping
    public String productList(Model model, HttpSession session) {

        User admin = (User) session.getAttribute("user");
        if (admin == null) return "redirect:/login";
        
        model.addAttribute("user", admin);

        model.addAttribute("products", productService.getAllProducts());
        return "admin/product-list";
    }

    @GetMapping("/add")
    public String addProductForm(Model model, HttpSession session) {

        User admin = (User) session.getAttribute("user");
        if (admin == null) return "redirect:/login";

        model.addAttribute("categories",  categoryService.getAllCategories());
        model.addAttribute("product",     new Product());
        return "admin/product-add";
    }

    @PostMapping("/add")
    public String saveProduct(
            @ModelAttribute Product product,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("user");
        if (admin == null) return "redirect:/login";

        try {
            productService.saveProduct(product, imageFiles);
            redirectAttributes.addFlashAttribute("success", "Product added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add product: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model, HttpSession session) {

        User admin = (User) session.getAttribute("user");
        if (admin == null) return "redirect:/login";

        model.addAttribute("product",    productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/product-edit";
    }

    // Update product
    @PostMapping("/edit/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("user");
        if (admin == null) return "redirect:/login";

        try {
            productService.updateProduct(id, product, imageFiles);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // Delete product
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("user");
        if (admin == null) return "redirect:/login";

        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("success", "Product deleted.");
        return "redirect:/admin/products";
    }

    @GetMapping("/subcategories/{categoryId}")
    @ResponseBody
    public List<SubCategory> getSubcategories(@PathVariable Long categoryId) {
        return subCategoryService.getByCategoryId(categoryId);
    }
}
