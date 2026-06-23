package com.ojasvi.ecommerce.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "newsletter_subscriber")
@Getter
@Setter
public class NewsletterSubscriber extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime subscribedAt;

    private LocalDateTime unsubscribedAt;

}
