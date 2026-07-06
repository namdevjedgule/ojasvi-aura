package com.ojasvi.ecommerce.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.Notification;
import com.ojasvi.ecommerce.Enum.NotificationType;
import com.ojasvi.ecommerce.Enum.RecipientType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	
	List<Notification> findByRecipientIdAndRecipientTypeOrderByCreatedAtDesc(
	        Long recipientId,
	        RecipientType recipientType);
	
	Page<Notification> findByRecipientIdAndRecipientTypeOrderByCreatedAtDesc(
            Long recipientId,
            RecipientType recipientType,
            Pageable pageable);

	long countByRecipientIdAndRecipientTypeAndIsReadFalse(
	        Long recipientId,
	        RecipientType recipientType);

	Optional<Notification> findById(Long id);
	
	long countByRecipientIdAndRecipientTypeAndNotificationType(
	        Long recipientId,
	        RecipientType recipientType,
	        NotificationType notificationType);
	
	Optional<Notification> findByIdAndRecipientIdAndRecipientType(
	        Long id,
	        Long recipientId,
	        RecipientType recipientType);

	long countByRecipientIdAndRecipientType(
	        Long recipientId,
	        RecipientType recipientType);

	void deleteByRecipientIdAndRecipientType(
	        Long recipientId,
	        RecipientType recipientType);

}
