package com.ojasvi.ecommerce.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ojasvi.ecommerce.DTO.ProductRequest;
import com.ojasvi.ecommerce.Entity.Category;
import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.ProductImage;
import com.ojasvi.ecommerce.Entity.SubCategory;
import com.ojasvi.ecommerce.Repository.CategoryRepository;
import com.ojasvi.ecommerce.Repository.ProductImageRepository;
import com.ojasvi.ecommerce.Repository.ProductRepository;
import com.ojasvi.ecommerce.Repository.SubCategoryRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAllWithImages();
    }

    public Product getProductByIdWithImages(Long id) {
        return productRepository.findByIdWithImages(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> getShopProducts() {
        return productRepository.findActiveProductsWithImages();
    }

    public List<Product> getActiveProductsBasic() {
        return productRepository.findByIsActiveTrue();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product getBySlug(String slug) {
        return productRepository.findBySlug(slug).orElse(null);
    }

    public Product getBySlugWithImages(String slug) {
        return productRepository.findBySlugWithImages(slug).orElse(null);
    }

    public List<Product> getByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndIsActiveTrue();
    }

    public List<Product> getRelated(Product product) {

        if (product.getSubCategory() == null) {
            return List.of();
        }

        Pageable top4 = PageRequest.of(0, 4);

        return productRepository.findRelatedWithImages(
                product.getSubCategory(), product.getId(), top4);
    }

    private String generateSku(Product product) {

        String categoryCode = product.getCategory().getCode();

        String subCategoryCode = product.getSubCategory().getCode();

        return categoryCode + "-" + subCategoryCode + "-" + String.format("%06d", product.getId());
    }

    private String generateSlug(String productName) {

        String baseSlug = productName.trim().toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        String slug = baseSlug;

        int counter = 1;

        while (productRepository.existsBySlug(slug)) {

            slug = baseSlug + "-" + counter++;
        }

        return slug;
    }

    @Transactional
    public void saveProduct(ProductRequest request, List<MultipartFile> imageFiles) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("Sub Category not found"));

        Product product = new Product();

        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setFabric(request.getFabric());
        product.setPrintType(request.getPrintType());
        product.setCollection(request.getCollection());
        product.setProductType(request.getProductType());
        product.setColor(request.getColor());
        product.setSize(request.getSize());
        product.setWeight(request.getWeight());
        product.setLength(request.getLength());
        product.setWidth(request.getWidth());

        product.setIsSet(request.getIsSet() != null ? request.getIsSet() : false);

        product.setSetContents(request.getSetContents());

        product.setDimensionUnit(request.getDimensionUnit());

        product.setCareInstructions(request.getCareInstructions());
        product.setTags(request.getTags());

        product.setMrp(request.getMrp());
        product.setSellingPrice(request.getSellingPrice());
        product.setStock(request.getStock());

        product.setFeatured(request.getFeatured() != null ? request.getFeatured() : false);

        product.setIsActive(request.getActive() != null ? request.getActive() : true);

        product.setCategory(category);
        product.setSubCategory(subCategory);

        product.setSlug(generateSlug(product.getProductName()));

        Product savedProduct = productRepository.save(product);

        savedProduct.setSku(generateSku(savedProduct));

        savedProduct = productRepository.save(savedProduct);

        saveImages(savedProduct, imageFiles);
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest request, List<MultipartFile> imageFiles) {

        Product existing = getProductById(id);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("Sub Category not found"));

        String oldName = existing.getProductName();

        boolean skuNeedsUpdate = !existing.getCategory().getId().equals(category.getId())
                || !existing.getSubCategory().getId().equals(subCategory.getId());

        existing.setProductName(request.getProductName());
        existing.setDescription(request.getDescription());
        existing.setFabric(request.getFabric());
        existing.setPrintType(request.getPrintType());
        existing.setCollection(request.getCollection());
        existing.setProductType(request.getProductType());
        existing.setSize(request.getSize());
        existing.setColor(request.getColor());
        existing.setWeight(request.getWeight());
        existing.setLength(request.getLength());
        existing.setWidth(request.getWidth());

        existing.setIsSet(request.getIsSet() != null ? request.getIsSet() : false);

        existing.setSetContents(request.getSetContents());

        existing.setDimensionUnit(request.getDimensionUnit());

        existing.setCareInstructions(request.getCareInstructions());
        existing.setTags(request.getTags());

        existing.setMrp(request.getMrp());
        existing.setSellingPrice(request.getSellingPrice());
        existing.setStock(request.getStock());

        existing.setFeatured(request.getFeatured() != null ? request.getFeatured() : false);
        existing.setIsActive(request.getActive() != null ? request.getActive() : true);

        existing.setCategory(category);
        existing.setSubCategory(subCategory);

        if (!oldName.equals(request.getProductName())) {

            existing.setSlug(generateSlug(request.getProductName()));
        }

        if (skuNeedsUpdate) {

            existing.setSku(generateSku(existing));
        }

        Product updatedProduct = productRepository.save(existing);

        if (imageFiles != null && !imageFiles.isEmpty()) {

            saveImages(updatedProduct, imageFiles);
        }

        return updatedProduct;
    }

    @Transactional
    public void deleteProduct(Long id) {

        Product product = getProductById(id);

        product.setIsActive(false);

        productRepository.save(product);
    }

    @Transactional
    public void toggleStatus(Long id) {

        Product product = getProductById(id);

        product.setIsActive(!Boolean.TRUE.equals(product.getIsActive()));

        productRepository.save(product);
    }

    @Transactional
    public void deleteProductImage(Long imageId) {

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Product product = image.getProduct();

        boolean wasPrimary = Boolean.TRUE.equals(image.getPrimaryImage());

        productImageRepository.delete(image);

        if (wasPrimary) {

            List<ProductImage> remainingImages = productImageRepository
                    .findByProductIdOrderByDisplayOrderAsc(product.getId());

            if (!remainingImages.isEmpty()) {

                ProductImage first = remainingImages.get(0);

                first.setPrimaryImage(true);

                productImageRepository.save(first);
            }
        }
    }

    public long countProducts() {
        return productRepository.count();
    }

    public long countActiveProducts() {
        return productRepository.countByIsActiveTrue();
    }

    public long countLowStockProducts() {
        return productRepository.countByStockLessThanEqualAndIsActiveTrue(10);
    }

    public long lowStockCount() {
        return productRepository.countByStockLessThanEqualAndIsActiveTrue(10);
    }

    private void saveImages(Product product, List<MultipartFile> imageFiles) {

        if (imageFiles == null || imageFiles.isEmpty()) {
            return;
        }

        List<ProductImage> existingImages = productImageRepository
                .findByProductIdOrderByDisplayOrderAsc(product.getId());

        int order = existingImages.size();

        boolean primaryExists = existingImages.stream().anyMatch(ProductImage::getPrimaryImage);

        int sequence = existingImages.size() + 1;

        for (MultipartFile file : imageFiles) {

            if (file == null || file.isEmpty()) {
                continue;
            }

            String imagePath = imageUploadService.upload(file, product.getProductName(), sequence);

            ProductImage image = new ProductImage();

            image.setProduct(product);
            image.setImageUrl(imagePath);
            image.setDisplayOrder(order++);
            image.setPrimaryImage(!primaryExists);

            productImageRepository.save(image);

            primaryExists = true;
            sequence++;
        }
    }

    public List<Product> getBestSellerProducts() {
        return productRepository.findTop8ByIsActiveTrueOrderBySoldCountDesc();
    }

    public List<Product> getFeaturedTopTwelveProducts() {
        return productRepository.findTop12ByFeaturedTrueAndIsActiveTrue();
    }

    public List<Product> getNewArrivalProducts() {
        return productRepository.findTop12ByIsActiveTrueOrderByCreatedAtDesc();
    }

    public long countInStock() {
        return productRepository.countByStockGreaterThan(10);
    }

    public long countLowStock() {
        return productRepository.countByStockBetween(1, 10);
    }

    public long countOutOfStock() {
        return productRepository.countByStock(0);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findByStockBetween(1, 10);
    }

    public List<Product> getOutOfStockProducts() {
        return productRepository.findByStock(0);
    }

    public void updateStock(Long productId, Integer newStock) {
        Product product = getProductById(productId);

        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        product.setStock(newStock);
        productRepository.save(product);
    }

    public void adjustStock(Long productId, Integer delta) {
        Product product = getProductById(productId);

        int currentStock = product.getStock() != null ? product.getStock() : 0;
        int newStock = currentStock + delta;

        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot go below zero");
        }

        product.setStock(newStock);
        productRepository.save(product);
    }

    public void toggleActiveStatus(Long productId) {
        Product product = getProductById(productId);
        product.setIsActive(!Boolean.TRUE.equals(product.getIsActive()));
        productRepository.save(product);
    }

    public String generateInventoryCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("Product Name,SKU,Category,Stock,Status\n");

        List<Product> products = productRepository.findAll();

        for (Product p : products) {
            String name = p.getProductName() != null ? p.getProductName().replace(",", " ") : "";
            String sku = p.getSku() != null ? p.getSku() : "";
            String category = p.getCategory() != null ? p.getCategory().getName() : "";
            int stock = p.getStock() != null ? p.getStock() : 0;
            String status = stock == 0 ? "Out of Stock" : (stock <= 10 ? "Low Stock" : "Good");

            sb.append(name).append(",")
              .append(sku).append(",")
              .append(category).append(",")
              .append(stock).append(",")
              .append(status).append("\n");
        }

        return sb.toString();
    }
}