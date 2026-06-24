package com.ojasvi.ecommerce.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findByUserId(Long userId);

	@Query("""
			SELECT DISTINCT c
			FROM Cart c
			LEFT JOIN FETCH c.items i
			LEFT JOIN FETCH i.product
			WHERE c.user.id = :userId
			""")
	Optional<Cart> findCartWithItems(@Param("userId") Long userId);
}
