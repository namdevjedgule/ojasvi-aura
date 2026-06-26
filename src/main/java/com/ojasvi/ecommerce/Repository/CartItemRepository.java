package com.ojasvi.ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.Cart;
import com.ojasvi.ecommerce.Entity.CartItem;
import com.ojasvi.ecommerce.Entity.Product;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    
    @Query("""
    	    SELECT ci FROM CartItem ci
    	    JOIN FETCH ci.product p
    	    LEFT JOIN FETCH p.images
    	    WHERE ci.cart.id = :cartId
    	""")
    	List<CartItem> findCartItemsWithProduct(@Param("cartId") Long cartId);
    
    Optional<CartItem> findByCartAndProductId(Cart cart, Long productId);
    
    void deleteByCartId(Long cartId);
    
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
