package com.ojasvi.ecommerce.Enum;

public enum OrderStatus {

    PENDING,
    CONFIRMED,
    PROCESSING,
    PACKED,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    
    CANCELLED,
    
    RETURN_REQUESTED,
    RETURNED,
    
    REFUND_PENDING,
    REFUNDED;
	
	public String getLabel() {
        return switch (this) {
            case PENDING -> "Pending";
            case CONFIRMED -> "Confirmed";
            case PROCESSING -> "Processing";
            case PACKED -> "Packed";
            case SHIPPED -> "Shipped";
            case OUT_FOR_DELIVERY -> "Out For Delivery";
            case DELIVERED -> "Delivered";
            case CANCELLED -> "Cancelled";
            case RETURN_REQUESTED -> "Return Requested";
            case RETURNED -> "Returned";
            case REFUND_PENDING -> "Refund Pending";
            case REFUNDED -> "Refunded";
        };
    }

    public String getCssClass() {
        return "badge-" + name().toLowerCase().replace("_", "-");
    }
}
