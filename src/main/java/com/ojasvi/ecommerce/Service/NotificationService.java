package com.ojasvi.ecommerce.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ojasvi.ecommerce.Entity.Notification;
import com.ojasvi.ecommerce.Enum.NotificationEvent;
import com.ojasvi.ecommerce.Enum.NotificationType;
import com.ojasvi.ecommerce.Enum.RecipientType;
import com.ojasvi.ecommerce.Enum.ReferenceType;
import com.ojasvi.ecommerce.Repository.NotificationRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class NotificationService {

	@Autowired
	private NotificationRepository notificationRepository;

	@Transactional
	public Notification createNotification(String title, String message, NotificationType notificationType,
			NotificationEvent notificationEvent, RecipientType recipientType, Long recipientId,
			ReferenceType referenceType, Long referenceId) {

		Notification notification = new Notification();

		notification.setTitle(title);
		notification.setMessage(message);
		notification.setNotificationType(notificationType);
		notification.setNotificationEvent(notificationEvent);
		notification.setRecipientType(recipientType);
		notification.setRecipientId(recipientId);
		notification.setReferenceType(referenceType);
		notification.setReferenceId(referenceId);
		notification.setIsRead(false);

		return notificationRepository.save(notification);
	}

	public Page<Notification> getNotifications(Long recipientId, RecipientType recipientType, Pageable pageable) {

		return notificationRepository.findByRecipientIdAndRecipientTypeOrderByCreatedAtDesc(recipientId, recipientType,
				pageable);
	}

	public long getUnreadCount(Long recipientId, RecipientType recipientType) {

		return notificationRepository.countByRecipientIdAndRecipientTypeAndIsReadFalse(recipientId, recipientType);
	}

	@Transactional
	public void markAsRead(Long notificationId,
	                       Long recipientId,
	                       RecipientType recipientType) {

		Notification notification = notificationRepository
		        .findByIdAndRecipientIdAndRecipientType(
		                notificationId,
		                recipientId,
		                recipientType)
		        .orElseThrow(() -> new RuntimeException("Notification not found"));

		notification.setIsRead(true);

		notificationRepository.save(notification);
	}

	@Transactional
	public void markAllAsRead(Long recipientId, RecipientType recipientType) {

		List<Notification> notifications = notificationRepository
				.findByRecipientIdAndRecipientTypeOrderByCreatedAtDesc(recipientId, recipientType);

		for (Notification notification : notifications) {
			notification.setIsRead(true);
		}

		notificationRepository.saveAll(notifications);
	}

	@Transactional
	public void deleteNotification(Long notificationId, Long recipientId, RecipientType recipientType) {

		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new RuntimeException("Notification not found"));

		if (!notification.getRecipientId().equals(recipientId) || notification.getRecipientType() != recipientType) {

			throw new RuntimeException("Unauthorized");
		}

		notificationRepository.delete(notification);
	}

	@Transactional
	public void clearAllNotifications(Long recipientId, RecipientType recipientType) {

		notificationRepository.deleteByRecipientIdAndRecipientType(
		        recipientId,
		        recipientType);
	}

	public long getCount(Long recipientId,
            RecipientType recipientType,
            NotificationType notificationType) {

return notificationRepository
   .countByRecipientIdAndRecipientTypeAndNotificationType(
           recipientId,
           recipientType,
           notificationType);
}

}
