package com.ojasvi.ecommerce.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Service.CategoryService;
import com.ojasvi.ecommerce.Service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/inventory")
public class AdminInventoryController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    private User getUserFromSession(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @GetMapping
    public String inventoryList(HttpSession session, Model model) {
    	
    	

        User admin = getUserFromSession(session);
        if (admin == null) return "redirect:/login";

        List<Product> products = productService.getAllProducts();

        model.addAttribute("user", admin);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());

        model.addAttribute("inStockCount",    productService.countInStock());
        model.addAttribute("lowStockCount",   productService.countLowStock());
        model.addAttribute("outOfStockCount", productService.countOutOfStock());

        return "admin/inventory";
    }

    @GetMapping("/filter")
    public String filterInventory(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,   
            HttpSession session,
            Model model) {

        User admin = getUserFromSession(session);
        if (admin == null) return "redirect:/login";

        List<Product> products;

        if (categoryId != null) {
            products = productService.getByCategory(categoryId);
        } else {
            products = productService.getAllProducts();
        }

        if (status != null) {
            switch (status) {
                case "low"  -> products = products.stream()
                        .filter(p -> p.getStock() != null && p.getStock() > 0 && p.getStock() <= 10)
                        .toList();
                case "out"  -> products = products.stream()
                        .filter(p -> p.getStock() != null && p.getStock() == 0)
                        .toList();
                case "active" -> products = products.stream()
                        .filter(p -> p.getStock() != null && p.getStock() > 10)
                        .toList();
            }
        }

        model.addAttribute("user", admin);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("inStockCount",    productService.countInStock());
        model.addAttribute("lowStockCount",   productService.countLowStock());
        model.addAttribute("outOfStockCount", productService.countOutOfStock());

        return "admin/inventory";
    }

    @PostMapping("/restock/{id}")
    public String restock(
            @PathVariable Long id,
            @RequestParam Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (getUserFromSession(session) == null) return "redirect:/login";

        try {
            productService.updateStock(id, quantity);
            redirectAttributes.addFlashAttribute("success",
                    "Stock updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to update stock: " + e.getMessage());
        }

        return "redirect:/admin/inventory";
    }

    @PostMapping("/adjust/{id}")
    public String adjustStock(
            @PathVariable Long id,
            @RequestParam Integer delta,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (getUserFromSession(session) == null) return "redirect:/login";

        try {
            productService.adjustStock(id, delta);
            redirectAttributes.addFlashAttribute("success", "Stock adjusted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to adjust stock: " + e.getMessage());
        }

        return "redirect:/admin/inventory";
    }

    @PostMapping("/bulk-restock")
    public String bulkRestock(
            @RequestParam List<Long> productIds,
            @RequestParam List<Integer> quantities,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (getUserFromSession(session) == null) return "redirect:/login";

        if (productIds.size() != quantities.size()) {
            redirectAttributes.addFlashAttribute("error", "Mismatched bulk update data.");
            return "redirect:/admin/inventory";
        }

        try {
            for (int i = 0; i < productIds.size(); i++) {
                productService.updateStock(productIds.get(i), quantities.get(i));
            }
            redirectAttributes.addFlashAttribute("success",
                    productIds.size() + " products updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Bulk update failed: " + e.getMessage());
        }

        return "redirect:/admin/inventory";
    }

    @GetMapping("/low-stock")
    @ResponseBody
    public List<Product> getLowStockProducts(HttpSession session) {
        if (getUserFromSession(session) == null) return List.of();
        return productService.getLowStockProducts();
    }

    @GetMapping("/out-of-stock")
    @ResponseBody
    public List<Product> getOutOfStockProducts(HttpSession session) {
        if (getUserFromSession(session) == null) return List.of();
        return productService.getOutOfStockProducts();
    }

    @GetMapping("/export")
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> exportInventory(HttpSession session) {

        if (getUserFromSession(session) == null) {
            return org.springframework.http.ResponseEntity.status(401).build();
        }

        String csv = productService.generateInventoryCsv();
        byte[] csvBytes = csv.getBytes();

        return org.springframework.http.ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=inventory.csv")
                .header("Content-Type", "text/csv")
                .body(csvBytes);
    }

    @PostMapping("/toggle/{id}")
    public String toggleActive(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (getUserFromSession(session) == null) return "redirect:/login";

        try {
            productService.toggleActiveStatus(id);
            redirectAttributes.addFlashAttribute("success", "Product status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to update status: " + e.getMessage());
        }

        return "redirect:/admin/inventory";
    }
}