package com.ojasvi.ecommerce.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Repository.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	private  OrderRepository orderRepository;
	
	public long countOrders() {
	    return orderRepository.count();
	}

	public List<Order> findRecentOrders() {
	    return orderRepository.findTop10RecentOrders();
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
    }
	
	public Order findByOrderNumber(String orderNumber) {

        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new RuntimeException("Order not found : " + orderNumber));
    }

}
