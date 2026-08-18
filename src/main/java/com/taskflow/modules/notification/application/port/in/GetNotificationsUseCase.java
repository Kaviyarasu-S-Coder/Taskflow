package com.taskflow.modules.notification.application.port.in;

import com.taskflow.modules.notification.application.dto.NotificationResponse;

import java.util.List;

public interface GetNotificationsUseCase {
    List<NotificationResponse> getNotificationsByRecipientId(Long recipientId);
    NotificationResponse markAsRead(Long notificationId);
}
