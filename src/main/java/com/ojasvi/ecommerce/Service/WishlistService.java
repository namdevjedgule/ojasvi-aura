package com.ojasvi.ecommerce.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ojasvi.ecommerce.DTO.WishlistDTO;
import com.ojasvi.ecommerce.Entity.Product;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Entity.Wishlist;
import com.ojasvi.ecommerce.Repository.ProductRepository;
import com.ojasvi.ecommerce.Repository.WishlistRepository;

@Service
public class WishlistService {

    @Autowired private WishlistRepository wishlistRepository;
    @Autowired private ProductRepository productRepository;

    public Map<String, Object> toggleWishlist(User user, Long productId) {

        Optional<Wishlist> existing =
                wishlistRepository.findByUserIdAndProductId(user.getId(), productId);

        if (existing.isPresent()) {
            wishlistRepository.delete(existing.get());
            return Map.of("inWishlist", false, "message", "Removed from wishlist");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Wishlist w = new Wishlist();
        w.setUser(user);
        w.setProduct(product);

        wishlistRepository.save(w);

        return Map.of("inWishlist", true, "message", "Added to wishlist");
    }
    
    @Transactional(readOnly = true)
    public List<WishlistDTO> getWishlist(User user) {
     
        List<Wishlist> wishlistItems = wishlistRepository.findByUserIdWithProductDetails(user.getId());
     
        return wishlistItems.stream()
                .map(w -> {
                    Product product = w.getProduct();
     
                    String imageUrl = (product.getImages() == null || product.getImages().isEmpty())
                            ? null
                            : product.getImages().get(0).getImageUrl();
     
                    String categoryName = product.getSubCategory() != null
                            ? product.getSubCategory().getName()
                            : (product.getCategory() != null ? product.getCategory().getName() : null);
     
                    return new WishlistDTO(
                            w.getId(),
                            product.getId(),
                            product.getProductName(),
                            imageUrl,
                            product.getSellingPrice(),
                            product.getSlug(),
                            product.getMrp(),
                            product.getStock(),
                            categoryName
                    );
                })
                .toList();
    }
}
