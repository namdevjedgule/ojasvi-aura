package com.ojasvi.ecommerce.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.ProductImage;
import com.ojasvi.ecommerce.Repository.ProductImageRepository;
import com.ojasvi.ecommerce.Repository.ProductRepository;

@Service
public class ProductService {

    @Autowired private ProductRepository       productRepository;
    @Autowired private ProductImageRepository  productImageRepository;
    @Autowired private ImageUploadService      imageUploadService;  // handles file saving

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByIsActiveTrue();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public Product getBySlug(String slug) {
        return productRepository.findBySlug(slug).orElse(null);
    }

    public List<Product> getByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getRelated(Product product) {
        return productRepository
                .findTop4BySubCategoryAndIdNotAndIsActiveTrue(
                        product.getSubCategory(), product.getId());
    }

    public void saveProduct(Product product, List<MultipartFile> imageFiles) {

        // Auto-generate slug if not set
        if (product.getSlug() == null || product.getSlug().isBlank()) {
            product.setSlug(generateSlug(product.getProductName()));
        }

        // Default isActive
        if (product.getIsActive() == null) product.setIsActive(true);

        Product saved = productRepository.save(product);

        // Save uploaded images
        saveImages(saved, imageFiles);
    }

    public void updateProduct(Long id, Product updated, List<MultipartFile> imageFiles) {

        Product existing = getProductById(id);

        existing.setProductName(updated.getProductName());
        existing.setDescription(updated.getDescription());
        existing.setMrp(updated.getMrp());
        existing.setSellingPrice(updated.getSellingPrice());
        existing.setStock(updated.getStock());
        existing.setFabric(updated.getFabric());
        existing.setPrintType(updated.getPrintType());
        existing.setSku(updated.getSku());
        existing.setColor(updated.getColor());
        existing.setSize(updated.getSize());
        existing.setWeight(updated.getWeight());
        existing.setFeatured(updated.getFeatured());
        existing.setIsActive(updated.getIsActive());
        existing.setMetaTitle(updated.getMetaTitle());
        existing.setMetaDescription(updated.getMetaDescription());
        existing.setCategory(updated.getCategory());
        existing.setSubCategory(updated.getSubCategory());

        // Regenerate slug only if name changed
        if (!existing.getProductName().equals(updated.getProductName())) {
            existing.setSlug(generateSlug(updated.getProductName()));
        }

        productRepository.save(existing);

        // Add new images if uploaded
        if (imageFiles != null && !imageFiles.isEmpty()) {
            saveImages(existing, imageFiles);
        }
    }

    public void deleteProduct(Long id) {
        // Soft delete — just set isActive = false
        Product product = getProductById(id);
        product.setIsActive(false);
        productRepository.save(product);
    }

    // ── Private helpers ───────────────────────────────────────────

    private void saveImages(Product product, List<MultipartFile> imageFiles) {
        if (imageFiles == null) return;

        boolean hasPrimary = product.getImages() != null &&
                product.getImages().stream().anyMatch(ProductImage::getPrimaryImage);

        int order = product.getImages() == null ? 0 : product.getImages().size();

        for (MultipartFile file : imageFiles) {
            if (file.isEmpty()) continue;

            String url = imageUploadService.upload(file);  // returns saved path/URL

            ProductImage img = new ProductImage();
            img.setProduct(product);
            img.setImageUrl(url);
            img.setDisplayOrder(order++);
            img.setPrimaryImage(!hasPrimary);  // first image becomes primary
            hasPrimary = true;

            productImageRepository.save(img);
        }
    }

    private String generateSlug(String name) {
        String base = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();

        // Ensure uniqueness
        String slug = base;
        int counter = 1;
        while (productRepository.findBySlug(slug).isPresent()) {
            slug = base + "-" + counter++;
        }
        return slug;
    }

    public long countProducts() {
        return productRepository.count();
    }

    public long lowStockCount() {
        return productRepository.countByStockLessThanEqual(10);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
