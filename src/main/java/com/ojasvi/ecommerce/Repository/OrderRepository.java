package com.ojasvi.ecommerce.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Enum.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            SELECT o
            FROM Order o
            JOIN FETCH o.customer
            ORDER BY o.createdAt DESC
            """)
    List<Order> findTop10RecentOrders();

    Optional<Order> findByOrderNumber(String orderNumber);

    @EntityGraph(attributePaths = {
            "customer",
            "shippingAddress",
            "orderItems",
            "orderItems.product"
    })
    Optional<Order> findWithDetailsById(Long id);
    
    @Query("""
    		SELECT DISTINCT o
    		FROM Order o
    		LEFT JOIN FETCH o.customer
    		LEFT JOIN FETCH o.orderItems oi
    		LEFT JOIN FETCH oi.product
    		LEFT JOIN FETCH o.shippingAddress
    		ORDER BY o.createdAt DESC
    		""")
    		List<Order> findAllWithDetails();

    long countByOrderStatus(OrderStatus status);
    
    long countByCustomerId(Long customerId);
    
    @Query("""
    		SELECT COALESCE(SUM(o.grandTotal),0)
    		FROM Order o
    		WHERE o.customer.id = :customerId
    		AND o.orderStatus = 'DELIVERED'
    		""")
    		BigDecimal getTotalAmountSpentByCustomer(Long customerId);
    
    List<Order> findTop5ByCustomerIdOrderByCreatedAtDesc(Long customerId);
    
    @Query("""
    		SELECT DISTINCT o
    		FROM Order o
    		LEFT JOIN FETCH o.orderItems
    		WHERE o.customer = :customer
    		ORDER BY o.createdAt DESC
    		""")
    		List<Order> findOrders(User customer);

}
