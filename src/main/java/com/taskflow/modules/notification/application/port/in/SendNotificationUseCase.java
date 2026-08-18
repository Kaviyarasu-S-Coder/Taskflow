package com.taskflow.modules.notification.application.port.in;

import com.taskflow.modules.notification.application.dto.NotificationResponse;

public interface SendNotificationUseCase {
    NotificationResponse sendNotification(SendNotificationCommand command);
}
