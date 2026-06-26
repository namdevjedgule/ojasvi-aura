package com.ojasvi.ecommerce.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ojasvi.ecommerce.DTO.OrderDetailsDto;
import com.ojasvi.ecommerce.DTO.OrderItemDto;
import com.ojasvi.ecommerce.DTO.OrderTrackingDTO;
import com.ojasvi.ecommerce.DTO.TimelineEvent;
import com.ojasvi.ecommerce.DTO.TrackingResponse;
import com.ojasvi.ecommerce.Entity.Address;
import com.ojasvi.ecommerce.Entity.Cart;
import com.ojasvi.ecommerce.Entity.CartItem;
import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Entity.OrderItem;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Enum.OrderStatus;
import com.ojasvi.ecommerce.Repository.CartItemRepository;
import com.ojasvi.ecommerce.Repository.CartRepository;
import com.ojasvi.ecommerce.Repository.OrderItemRepository;
import com.ojasvi.ecommerce.Repository.OrderRepository;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");
    
    public List<Order> findRecentOrders() {
	    return orderRepository.findTop10RecentOrders();
	}
    
    public Order findByOrderNumber(String orderNumber) {

        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new RuntimeException("Order not found : " + orderNumber));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllWithDetails();
    }

    public long countOrders() {
        return orderRepository.count();
    }

    public long countPendingOrders() {
        return orderRepository.countByOrderStatus(OrderStatus.PENDING);
    }

    public long countConfirmedOrders() {
        return orderRepository.countByOrderStatus(OrderStatus.CONFIRMED);
    }

    public long countPackedOrders() {
        return orderRepository.countByOrderStatus(OrderStatus.PACKED);
    }

    public long countShippedOrders() {
        return orderRepository.countByOrderStatus(OrderStatus.SHIPPED);
    }

    public long countDeliveredOrders() {
        return orderRepository.countByOrderStatus(OrderStatus.DELIVERED);
    }

    public long countCancelledOrders() {
        return orderRepository.countByOrderStatus(OrderStatus.CANCELLED);
    }

    public OrderDetailsDto getOrderDetails(Long id) {

        Order order = orderRepository.findWithDetailsById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderDetailsDto dto = new OrderDetailsDto();

        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());

        User customer = order.getCustomer();

        if (customer != null) {

            dto.setCustomerName(customer.getFullName());

            dto.setCustomerEmail(customer.getEmail());

            dto.setCustomerPhone(customer.getMobile());
        }

        if (order.getCreatedAt() != null) {
            dto.setCreatedAt(order.getCreatedAt().format(FORMATTER));
        }

        Address address = order.getShippingAddress();

        if (address != null) {

            dto.setAddressLine1(address.getAddressLine1());

            dto.setAddressLine2(address.getAddressLine2());

            dto.setLandmark(address.getLandmark());

            dto.setCity(address.getCity());

            dto.setState(address.getState());

            dto.setCountry(address.getCountry());

            dto.setPincode(address.getPincode());

        }

        dto.setSubtotal(order.getSubtotal());

        dto.setShippingCharge(order.getShippingCharge());

        dto.setDiscountAmount(order.getDiscountAmount());

        dto.setTaxAmount(order.getTaxAmount());

        dto.setGrandTotal(order.getGrandTotal());

        dto.setRemarks(order.getRemarks());

        dto.setPaymentMethod(order.getPaymentMethod().name());

        dto.setPaymentStatus(order.getPaymentStatus().name());

        dto.setOrderStatus(order.getOrderStatus().name());

        for (OrderItem item : order.getOrderItems()) {

            OrderItemDto itemDto = new OrderItemDto();

            itemDto.setId(item.getId());

            itemDto.setProductName(item.getProductName());

            itemDto.setImageUrl(item.getProductImage());

            itemDto.setPrice(item.getProductPrice());

            itemDto.setQuantity(item.getQuantity());

            itemDto.setSubtotal(item.getSubtotal());

            dto.getItems().add(itemDto);

        }

        return dto;

    }

    public void updateOrderStatus(Long id, String status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus newStatus = OrderStatus.valueOf(status);

        order.setOrderStatus(newStatus);

        orderRepository.save(order);

    }
    
    public long countOrdersByCustomer(Long customerId) {

        return orderRepository.countByCustomerId(customerId);

    }

    public BigDecimal getTotalAmountSpentByCustomer(Long customerId) {

        BigDecimal amount =
                orderRepository.getTotalAmountSpentByCustomer(customerId);

        return amount != null ? amount : BigDecimal.ZERO;

    }

    public List<Order> findRecentOrdersByCustomer(Long customerId) {

        return orderRepository.findTop5ByCustomerIdOrderByCreatedAtDesc(customerId);

    }
    
    public List<Order> getCustomerOrders(User customer) {

        return orderRepository.findOrders(customer);

    }
    
    public Map<String, Object> cancelOrder(Long orderId, User customer) {

        Map<String, Object> response = new HashMap<>();

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            response.put("success", false);
            response.put("message", "Order not found.");
            return response;
        }

        Order order = optionalOrder.get();

        if (!order.getCustomer().getId().equals(customer.getId())) {
            response.put("success", false);
            response.put("message", "You are not authorized.");
            return response;
        }

        if (!(order.getOrderStatus() == OrderStatus.PENDING
                || order.getOrderStatus() == OrderStatus.CONFIRMED)) {

            response.put("success", false);
            response.put("message", "This order cannot be cancelled.");
            return response;
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        response.put("success", true);
        response.put("message", "Order cancelled successfully.");

        return response;
    }
    
    public Map<String, Object> reorder(Long orderId, User user) {

        Map<String, Object> response = new HashMap<>();

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {

            response.put("success", false);
            response.put("message", "Order not found.");

            return response;
        }

        Order order = optionalOrder.get();

        if (!order.getCustomer().getId().equals(user.getId())) {

            response.put("success", false);
            response.put("message", "Unauthorized.");

            return response;
        }

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);

                });

        for (OrderItem orderItem : order.getOrderItems()) {

            Optional<CartItem> existing =
                    cartItemRepository.findByCartAndProduct(cart,
                            orderItem.getProduct());

            if (existing.isPresent()) {

                CartItem item = existing.get();

                item.setQuantity(item.getQuantity() + orderItem.getQuantity());

                cartItemRepository.save(item);

            } else {

                CartItem item = new CartItem();

                item.setCart(cart);

                item.setProduct(orderItem.getProduct());

                item.setQuantity(orderItem.getQuantity());

                cartItemRepository.save(item);

            }

        }

        response.put("success", true);
        response.put("message", "Items added to cart.");

        return response;
    }
    
    public TrackingResponse getTracking(Long orderId, User customer) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        TrackingResponse response = new TrackingResponse();

        response.setOrderNumber(order.getOrderNumber());
        response.setOrderStatus(order.getOrderStatus());
        response.setTrackingNumber(order.getTrackingNumber());
        response.setCourierName(order.getCourierName());
        response.setShippedDate(order.getShippedDate());
        response.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        response.setDeliveredDate(order.getDeliveredDate());

        return response;
    }
    
    public OrderTrackingDTO getTrackingTimeline(Order order) {

        boolean confirmed = false;
        boolean processing = false;
        boolean packed = false;
        boolean shipped = false;
        boolean outForDelivery = false;
        boolean delivered = false;

        boolean cancelled = false;

        boolean returnRequested = false;
        boolean returned = false;
        boolean refundPending = false;
        boolean refunded = false;

        switch (order.getOrderStatus()) {

            case PENDING:
                break;

            case CONFIRMED:
                confirmed = true;
                break;

            case PROCESSING:
                confirmed = true;
                processing = true;
                break;

            case PACKED:
                confirmed = true;
                processing = true;
                packed = true;
                break;

            case SHIPPED:
                confirmed = true;
                processing = true;
                packed = true;
                shipped = true;
                break;

            case OUT_FOR_DELIVERY:
                confirmed = true;
                processing = true;
                packed = true;
                shipped = true;
                outForDelivery = true;
                break;

            case DELIVERED:
                confirmed = true;
                processing = true;
                packed = true;
                shipped = true;
                outForDelivery = true;
                delivered = true;
                break;

            case CANCELLED:
                cancelled = true;
                break;

            case RETURN_REQUESTED:
                confirmed = true;
                processing = true;
                packed = true;
                shipped = true;
                outForDelivery = true;
                delivered = true;
                returnRequested = true;
                break;

            case RETURNED:
                confirmed = true;
                processing = true;
                packed = true;
                shipped = true;
                outForDelivery = true;
                delivered = true;
                returnRequested = true;
                returned = true;
                break;

            case REFUND_PENDING:
                confirmed = true;
                processing = true;
                packed = true;
                shipped = true;
                outForDelivery = true;
                delivered = true;
                returnRequested = true;
                returned = true;
                refundPending = true;
                break;

            case REFUNDED:
                confirmed = true;
                processing = true;
                packed = true;
                shipped = true;
                outForDelivery = true;
                delivered = true;
                returnRequested = true;
                returned = true;
                refundPending = true;
                refunded = true;
                break;
        }

        boolean returnFlow =
                returnRequested || returned || refundPending || refunded;

        return new OrderTrackingDTO(
                confirmed,
                processing,
                packed,
                shipped,
                outForDelivery,
                delivered,
                cancelled,
                returnRequested,
                returned,
                refundPending,
                refunded,
                returnFlow
        );
    }
    
    public List<TimelineEvent> getTimeline(Long orderId, User customer) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        List<TimelineEvent> timeline = new ArrayList<>();

        timeline.add(new TimelineEvent(
                "Order Placed",
                order.getCreatedAt().toLocalDate().toString(),
                true));

        timeline.add(new TimelineEvent(
                "Payment Confirmed",
                order.getCreatedAt().toLocalDate().toString(),
                order.getOrderStatus().ordinal() >= OrderStatus.CONFIRMED.ordinal()));

        timeline.add(new TimelineEvent(
                "Packed",
                "",
                order.getOrderStatus().ordinal() >= OrderStatus.PACKED.ordinal()));

        timeline.add(new TimelineEvent(
                "Shipped",
                order.getShippedDate() == null ? "" : order.getShippedDate().toString(),
                order.getOrderStatus().ordinal() >= OrderStatus.SHIPPED.ordinal()));

        timeline.add(new TimelineEvent(
                "Out For Delivery",
                "",
                order.getOrderStatus().ordinal() >= OrderStatus.OUT_FOR_DELIVERY.ordinal()));

        timeline.add(new TimelineEvent(
                "Delivered",
                order.getDeliveredDate() == null ? "" : order.getDeliveredDate().toString(),
                order.getOrderStatus() == OrderStatus.DELIVERED));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {

            timeline.add(new TimelineEvent(
                    "Cancelled",
                    "",
                    true));

        }

        return timeline;
    }

}
