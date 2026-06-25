package com.ojasvi.ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	@Query("SELECT o FROM Order o JOIN FETCH o.customer ORDER BY o.createdAt DESC")
	List<Order> findTop10RecentOrders();
	
	Optional<Order> findByOrderNumber(String orderNumber);
}
