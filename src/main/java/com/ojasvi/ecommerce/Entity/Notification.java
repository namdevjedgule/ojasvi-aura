package com.ojasvi.ecommerce.Entity;

import com.ojasvi.ecommerce.Enum.NotificationEvent;
import com.ojasvi.ecommerce.Enum.NotificationType;
import com.ojasvi.ecommerce.Enum.RecipientType;
import com.ojasvi.ecommerce.Enum.ReferenceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="notifications")
@Getter
@Setter
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String message;

    private Long recipientId;

    private Long referenceId;

    private Boolean isRead = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationEvent notificationEvent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecipientType recipientType;

    @Enumerated(EnumType.STRING)
    @Column(nullable =false)
    private ReferenceType referenceType;

}
