package com.taskflow.modules.notification.application.dto;

public record NotificationResponse(
        Long id,
        String notificationCode,
        Long recipientId,
        String title,
        String message,
        String targetEntityType,
        Long targetEntityId,
        Boolean isRead
) {}
