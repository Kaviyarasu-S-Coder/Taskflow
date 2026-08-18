package com.taskflow.modules.notification.application.service;

import com.taskflow.common.exception.ResourceNotFoundException;
import com.taskflow.modules.notification.application.dto.NotificationResponse;
import com.taskflow.modules.notification.application.port.in.*;
import com.taskflow.modules.notification.domain.Notification;
import com.taskflow.modules.notification.domain.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationApplicationService implements SendNotificationUseCase, GetNotificationsUseCase {

    private final NotificationRepository notificationRepository;

    public NotificationApplicationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationResponse sendNotification(SendNotificationCommand command) {
        Notification notification = Notification.create(
                command.recipientId(),
                command.title(),
                command.message(),
                command.targetEntityType(),
                command.targetEntityId()
        );

        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByRecipientId(Long recipientId) {
        return notificationRepository.findByRecipientId(recipientId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        notification.markAsRead();
        Notification updated = notificationRepository.save(notification);
        return mapToResponse(updated);
    }

    private NotificationResponse mapToResponse(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getNotificationCode(),
                n.getRecipientId(),
                n.getTitle(),
                n.getMessage(),
                n.getTargetEntityType(),
                n.getTargetEntityId(),
                n.getIsRead()
        );
    }
}
