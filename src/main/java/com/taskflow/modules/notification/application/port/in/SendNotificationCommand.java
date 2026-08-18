package com.taskflow.modules.notification.application.port.in;

public record SendNotificationCommand(
        Long recipientId,
        String title,
        String message,
        String targetEntityType,
        Long targetEntityId
) {}
