package com.ojasvi.ecommerce.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "newsletter_campaign")
@Getter
@Setter
public class NewsletterCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String recipientType;

    private Integer recipientCount;

    private String status;

    private LocalDateTime sentAt;
}
