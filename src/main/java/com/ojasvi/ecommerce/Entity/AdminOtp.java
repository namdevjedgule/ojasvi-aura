package com.ojasvi.ecommerce.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "admin_otps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminOtp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    public AdminOtp(String email, String otp, LocalDateTime expiresAt) {
        this.email     = email;
        this.otp       = otp;
        this.expiresAt = expiresAt;
        this.used      = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}
