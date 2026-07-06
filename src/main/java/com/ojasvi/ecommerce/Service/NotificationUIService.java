package com.ojasvi.ecommerce.Service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Enum.NotificationEvent;
import com.ojasvi.ecommerce.Enum.NotificationType;

@Service
public class NotificationUIService {

    private static final Map<NotificationType, String> COLOR_MAP = Map.ofEntries(

            Map.entry(NotificationType.ORDER, "type-order"),
            Map.entry(NotificationType.PAYMENT, "type-payment"),
            Map.entry(NotificationType.RETURN, "type-return"),
            Map.entry(NotificationType.REFUND, "type-refund"),
            Map.entry(NotificationType.PRODUCT, "type-product"),
            Map.entry(NotificationType.STOCK, "type-stock"),
            Map.entry(NotificationType.COUPON, "type-coupon"),
            Map.entry(NotificationType.OFFER, "type-offer"),
            Map.entry(NotificationType.REVIEW, "type-review"),
            Map.entry(NotificationType.SUPPORT, "type-support"),
            Map.entry(NotificationType.USER, "type-user"),
            Map.entry(NotificationType.SYSTEM, "type-system")

    );

    /* ===============================
       Notification Event -> FontAwesome Icon
       =============================== */
    private static final Map<NotificationEvent, String> ICON_MAP = Map.ofEntries(

            // ================= USER =================
            Map.entry(NotificationEvent.WELCOME, "fa-solid fa-handshake"),
            Map.entry(NotificationEvent.ACCOUNT_CREATED, "fa-solid fa-user-check"),
            Map.entry(NotificationEvent.ACCOUNT_VERIFIED, "fa-solid fa-user-shield"),
            Map.entry(NotificationEvent.ACCOUNT_UPDATED, "fa-solid fa-user-pen"),
            Map.entry(NotificationEvent.PASSWORD_CHANGED, "fa-solid fa-key"),
            Map.entry(NotificationEvent.PASSWORD_RESET, "fa-solid fa-unlock-keyhole"),
            Map.entry(NotificationEvent.EMAIL_CHANGED, "fa-solid fa-envelope-circle-check"),
            Map.entry(NotificationEvent.MOBILE_CHANGED, "fa-solid fa-mobile-screen-button"),

            Map.entry(NotificationEvent.LOGIN_SUCCESS, "fa-solid fa-right-to-bracket"),
            Map.entry(NotificationEvent.LOGIN_FAILED, "fa-solid fa-circle-xmark"),
            Map.entry(NotificationEvent.NEW_DEVICE_LOGIN, "fa-solid fa-laptop"),
            Map.entry(NotificationEvent.ACCOUNT_LOCKED, "fa-solid fa-lock"),
            Map.entry(NotificationEvent.ACCOUNT_UNLOCKED, "fa-solid fa-lock-open"),
            Map.entry(NotificationEvent.USER_BLOCKED, "fa-solid fa-user-slash"),
            Map.entry(NotificationEvent.USER_UNBLOCKED, "fa-solid fa-user-check"),

            // ================= ORDERS =================
            Map.entry(NotificationEvent.ORDER_PLACED, "fa-solid fa-cart-shopping"),
            Map.entry(NotificationEvent.ORDER_CONFIRMED, "fa-solid fa-circle-check"),
            Map.entry(NotificationEvent.ORDER_PROCESSING, "fa-solid fa-gears"),
            Map.entry(NotificationEvent.ORDER_PACKED, "fa-solid fa-box"),
            Map.entry(NotificationEvent.ORDER_SHIPPED, "fa-solid fa-truck-fast"),
            Map.entry(NotificationEvent.OUT_FOR_DELIVERY, "fa-solid fa-motorcycle"),
            Map.entry(NotificationEvent.DELIVERED, "fa-solid fa-box-open"),
            Map.entry(NotificationEvent.ORDER_CANCELLED, "fa-solid fa-ban"),
            Map.entry(NotificationEvent.ORDER_DELAYED, "fa-solid fa-clock"),
            Map.entry(NotificationEvent.ORDER_COMPLETED, "fa-solid fa-circle-check"),

            // ================= PAYMENT =================
            Map.entry(NotificationEvent.PAYMENT_PENDING, "fa-solid fa-hourglass-half"),
            Map.entry(NotificationEvent.PAYMENT_SUCCESS, "fa-solid fa-credit-card"),
            Map.entry(NotificationEvent.PAYMENT_FAILED, "fa-solid fa-circle-xmark"),
            Map.entry(NotificationEvent.PAYMENT_REFUNDED, "fa-solid fa-money-bill-transfer"),
            Map.entry(NotificationEvent.PAYMENT_RECEIVED, "fa-solid fa-money-check-dollar"),
            Map.entry(NotificationEvent.PAYMENT_VERIFICATION_REQUIRED, "fa-solid fa-shield-halved"),

            // ================= RETURNS =================
            Map.entry(NotificationEvent.RETURN_REQUESTED, "fa-solid fa-rotate-left"),
            Map.entry(NotificationEvent.RETURN_APPROVED, "fa-solid fa-check"),
            Map.entry(NotificationEvent.RETURN_REJECTED, "fa-solid fa-xmark"),
            Map.entry(NotificationEvent.PRODUCT_PICKUP_SCHEDULED, "fa-solid fa-calendar-check"),
            Map.entry(NotificationEvent.PRODUCT_PICKED_UP, "fa-solid fa-box-open"),
            Map.entry(NotificationEvent.REFUND_PENDING, "fa-solid fa-hourglass-half"),
            Map.entry(NotificationEvent.REFUND_INITIATED, "fa-solid fa-money-bill-wave"),
            Map.entry(NotificationEvent.REFUND_COMPLETED, "fa-solid fa-money-bill-transfer"),
            Map.entry(NotificationEvent.REFUND_FAILED, "fa-solid fa-circle-xmark"),

            // ================= CART =================
            Map.entry(NotificationEvent.CART_REMINDER, "fa-solid fa-cart-arrow-down"),
            Map.entry(NotificationEvent.WISHLIST_PRICE_DROP, "fa-solid fa-heart-circle-bolt"),
            Map.entry(NotificationEvent.PRODUCT_BACK_IN_STOCK, "fa-solid fa-boxes-stacked"),
            Map.entry(NotificationEvent.CART_ITEM_LOW_STOCK, "fa-solid fa-triangle-exclamation"),

            // ================= PRODUCT =================
            Map.entry(NotificationEvent.PRODUCT_CREATED, "fa-solid fa-box-open"),
            Map.entry(NotificationEvent.PRODUCT_UPDATED, "fa-solid fa-pen"),
            Map.entry(NotificationEvent.PRODUCT_DELETED, "fa-solid fa-trash"),
            Map.entry(NotificationEvent.PRODUCT_AVAILABLE, "fa-solid fa-check"),
            Map.entry(NotificationEvent.PRODUCT_DISCONTINUED, "fa-solid fa-ban"),

            // ================= COUPONS =================
            Map.entry(NotificationEvent.COUPON_CREATED, "fa-solid fa-ticket"),
            Map.entry(NotificationEvent.COUPON_RECEIVED, "fa-solid fa-tags"),
            Map.entry(NotificationEvent.COUPON_EXPIRING, "fa-solid fa-hourglass-end"),
            Map.entry(NotificationEvent.COUPON_EXPIRED, "fa-solid fa-ticket-slash"),

            // ================= OFFERS =================
            Map.entry(NotificationEvent.OFFER, "fa-solid fa-gift"),
            Map.entry(NotificationEvent.FLASH_SALE, "fa-solid fa-bolt"),
            Map.entry(NotificationEvent.FESTIVAL_OFFER, "fa-solid fa-gifts"),

            // ================= REVIEWS =================
            Map.entry(NotificationEvent.NEW_REVIEW, "fa-solid fa-star"),
            Map.entry(NotificationEvent.NEW_REVIEW_REPLY, "fa-solid fa-reply"),
            Map.entry(NotificationEvent.REVIEW_REQUEST, "fa-solid fa-comment-dots"),
            Map.entry(NotificationEvent.REVIEW_APPROVED, "fa-solid fa-circle-check"),
            Map.entry(NotificationEvent.REVIEW_REJECTED, "fa-solid fa-circle-xmark"),

            // ================= SHIPPING =================
            Map.entry(NotificationEvent.SHIPPING_ADDRESS_UPDATED, "fa-solid fa-location-dot"),
            Map.entry(NotificationEvent.DELIVERY_ATTEMPT_FAILED, "fa-solid fa-triangle-exclamation"),

            // ================= ADMIN =================
            Map.entry(NotificationEvent.NEW_ORDER, "fa-solid fa-cart-plus"),
            Map.entry(NotificationEvent.NEW_USER_REGISTERED, "fa-solid fa-user-plus"),
            Map.entry(NotificationEvent.NEW_SELLER_REGISTERED, "fa-solid fa-store"),
            Map.entry(NotificationEvent.NEW_CONTACT_MESSAGE, "fa-solid fa-envelope"),
            Map.entry(NotificationEvent.NEW_RETURN_REQUEST, "fa-solid fa-rotate-left"),
            Map.entry(NotificationEvent.NEW_REFUND_REQUEST, "fa-solid fa-money-bill-transfer"),

            // ================= INVENTORY =================
            Map.entry(NotificationEvent.LOW_STOCK, "fa-solid fa-triangle-exclamation"),
            Map.entry(NotificationEvent.OUT_OF_STOCK, "fa-solid fa-ban"),
            Map.entry(NotificationEvent.PRODUCT_RESTOCKED, "fa-solid fa-boxes-stacked"),
            Map.entry(NotificationEvent.INVENTORY_UPDATED, "fa-solid fa-warehouse"),

            // ================= ORDER MANAGEMENT =================
            Map.entry(NotificationEvent.ORDER_ASSIGNED, "fa-solid fa-user-check"),
            Map.entry(NotificationEvent.ORDER_CANCELLED_BY_CUSTOMER, "fa-solid fa-user-xmark"),
            Map.entry(NotificationEvent.ORDER_CANCELLED_BY_ADMIN, "fa-solid fa-user-shield"),

            // ================= SUPPORT =================
            Map.entry(NotificationEvent.SUPPORT_TICKET_CREATED, "fa-solid fa-headset"),
            Map.entry(NotificationEvent.SUPPORT_TICKET_UPDATED, "fa-solid fa-comments"),
            Map.entry(NotificationEvent.SUPPORT_TICKET_CLOSED, "fa-solid fa-circle-check"),

            // ================= SYSTEM =================
            Map.entry(NotificationEvent.SYSTEM_MAINTENANCE, "fa-solid fa-screwdriver-wrench"),
            Map.entry(NotificationEvent.APPLICATION_UPDATE, "fa-solid fa-download"),
            Map.entry(NotificationEvent.GENERAL_ANNOUNCEMENT, "fa-solid fa-bullhorn")

    );
    
    public String getColorClass(NotificationType type) {

        return COLOR_MAP.getOrDefault(type, "type-system");
    }

    public String getIcon(NotificationEvent event) {

        return ICON_MAP.getOrDefault(event, "fa-solid fa-bell");
    }

}