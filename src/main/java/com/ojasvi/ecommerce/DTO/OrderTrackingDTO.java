package com.ojasvi.ecommerce.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderTrackingDTO {

    private boolean confirmed;
    private boolean processing;
    private boolean packed;
    private boolean shipped;
    private boolean outForDelivery;
    private boolean delivered;
    
    private boolean cancelled;
    
    private boolean returnRequested;
    private boolean returned;
    private boolean refundPending;
    private boolean refunded;
    
    private boolean returnFlow;

}
