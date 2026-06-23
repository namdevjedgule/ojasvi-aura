package com.ojasvi.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "store_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName        = "Ojasvi";
    private String tagline          = "Luxury Home Linen";
    private String storeEmail;
    private String phone;
    private String currency         = "INR";
    private String gstNumber;

    @Column(length = 1000)
    private String description;

    private String addressLine1;
    private String city;
    private String state;
    private String pincode;
    private String country          = "India";

    private String logoPath;
    private String faviconPath;
    private String primaryColor     = "#C5A47E";
    private String brownColor       = "#5C3013";
    private String bgColor          = "#F5F1EB";

    private String supportEmail;
    private String supportPhone;
    private String instagram;
    private String facebook;
    private String whatsapp;
    private String supportHours     = "Mon–Sat, 10am–6pm";

    private Boolean notifyNewOrder      = true;
    private Boolean notifyOrderCancel   = true;
    private Boolean notifyLowStock      = true;
    private Boolean notifyNewCustomer   = false;
    private Boolean notifyPaymentFailed = true;

    private String  metaTitle           = "Ojasvi — Luxury Home Linen";

    @Column(length = 500)
    private String  metaDescription;

    @Column(length = 500)
    private String  metaKeywords;

    private String  googleAnalyticsId;

    private Boolean maintenanceMode     = false;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
