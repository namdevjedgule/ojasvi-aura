package com.ojasvi.ecommerce.Controller;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import com.ojasvi.ecommerce.DTO.ProductRequest;
import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.SubCategory;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.AccessValidator;
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
        
        if (!AccessValidator.isAdmin(admin)) {
            return "redirect:/customer-dashboard";
        }
        
        List<Product> products = productService.getAllProducts();

        model.addAttribute("user", admin);
        model.addAttribute("products", products);

        model.addAttribute("totalProducts", productService.countProducts());

        model.addAttribute("activeProducts", productService.countActiveProducts());

        model.addAttribute("lowStockProducts",productService.countLowStockProducts());
        
        model.addAttribute("categories", categoryService.getAllCategories());
        
        model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
        
        return "admin/product-list";
    }

    @GetMapping("/add")
    public String addProductForm(Model model, HttpSession session) {

        User admin = (User) session.getAttribute("user");
        if (admin == null) return "redirect:/login";
        
        if (!AccessValidator.isAdmin(admin)) {
            return "redirect:/customer-dashboard";
        }
        
        model.addAttribute("user", admin);
        
        model.addAttribute("product", new Product());

        model.addAttribute("categories", categoryService.getAllCategories());
        
        model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
           
        return "admin/product-add";
    }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<Map<String, String>> saveProduct(
            @ModelAttribute ProductRequest request,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            HttpSession session) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return ResponseEntity.status(401)
                .body(Map.of("message", "Unauthorized"));
        }

        try {
            productService.saveProduct(request, imageFiles);
            return ResponseEntity.ok(Map.of("message", "Product added successfully!"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Failed to add product: " + e.getMessage()));
        }
    }
    
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateProduct(
            @ModelAttribute ProductRequest request,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            HttpSession session) {

        User admin = (User) session.getAttribute("user");
        if (admin == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        try {
            productService.updateProduct(request.getId(), request, imageFiles);
            return ResponseEntity.ok(Map.of("message", "Product updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to update: " + e.getMessage()));
        }
    }
    
    @GetMapping("/data/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProductData(@PathVariable Long id) {
    	Product p = productService.getProductByIdWithImages(id);
    	
        Map<String, Object> data = new HashMap<>();
        data.put("id", p.getId());
        data.put("productName", p.getProductName());
        data.put("description", p.getDescription());
        data.put("fabric", p.getFabric());
        data.put("printType", p.getPrintType());
        data.put("collection", p.getCollection());
        data.put("productType", p.getProductType());
        data.put("color", p.getColor());
        data.put("size", p.getSize());
        data.put("isSet", p.getIsSet());
        data.put("setContents", p.getSetContents());
        data.put("weight", p.getWeight());
        data.put("mrp", p.getMrp());
        data.put("sellingPrice", p.getSellingPrice());
        data.put("length", p.getLength());
        data.put("width", p.getWidth());
        data.put("dimensionUnit", p.getDimensionUnit());
        data.put("careInstructions", p.getCareInstructions());
        data.put("tags", p.getTags());
        data.put("stock", p.getStock());
        data.put("featured", p.getFeatured());
        data.put("active", p.getIsActive());
        data.put("categoryId", p.getCategory() != null ? p.getCategory().getId() : null);
        data.put("subCategoryId", p.getSubCategory() != null ? p.getSubCategory().getId() : null);
        
        List<Map<String, Object>> images = p.getImages().stream()
                .map(img -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", img.getId());
                    m.put("imageUrl", img.getImageUrl());
                    m.put("primary", img.getPrimaryImage());
                    return m;
                })
                .collect(Collectors.toList());
            data.put("images", images);
            
        return ResponseEntity.ok(data);
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("user");
        if (admin == null) return "redirect:/login";
        
        Product product =
                productService.getProductById(id);

        if (product == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Product not found");

            return "redirect:/admin/products";
        }
        
        model.addAttribute("user", admin);

        model.addAttribute("product", product);
        
        model.addAttribute("categories", categoryService.getAllCategories());
        
        model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
                
        return "admin/products";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductRequest request,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("user");
        if (admin == null) return "redirect:/login";

        try {
            productService.updateProduct(id, request, imageFiles);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }
    
    @GetMapping("/view/{id}")
    public String viewProduct(
            @PathVariable Long id,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return "redirect:/login";
        }

        Product product =
                productService.getProductById(id);

        if (product == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Product not found");

            return "redirect:/admin/products";
        }

        model.addAttribute("user", admin);
        model.addAttribute("product", product);

        return "admin/product-view";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("user");
        if (admin == null) return "redirect:/login";

        try {

            productService.deleteProduct(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Product deleted successfully");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }
        
        return "redirect:/admin/products";
    }
    
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        productService.toggleStatus(id);

        redirectAttributes.addFlashAttribute(
                "success",
                "Status updated");

        return "redirect:/admin/products";
    }
    
    @PostMapping("/image/delete/{imageId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable Long imageId) {
        try {
            productService.deleteProductImage(imageId);
            return ResponseEntity.ok(Map.of("message", "Image removed"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to remove image"));
        }
    }
    
    @GetMapping("/subcategories/{categoryId}")
    @ResponseBody
    public List<SubCategory> getSubcategories(@PathVariable Long categoryId) {
        return subCategoryService.getByCategoryId(categoryId);
    }
}
