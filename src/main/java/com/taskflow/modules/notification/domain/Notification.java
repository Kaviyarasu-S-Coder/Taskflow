package com.taskflow.modules.notification.domain;

import java.util.UUID;

public class Notification {

    private Long id;
    private String notificationCode;
    private Long recipientId;
    private String title;
    private String message;
    private String targetEntityType;
    private Long targetEntityId;
    private Boolean isRead;

    public static Notification create(Long recipientId, String title, String message, String targetEntityType, Long targetEntityId) {
        Notification n = new Notification();
        n.notificationCode = "NTF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        n.recipientId = recipientId;
        n.title = title;
        n.message = message;
        n.targetEntityType = targetEntityType;
        n.targetEntityId = targetEntityId;
        n.isRead = false;
        return n;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNotificationCode() { return notificationCode; }
    public void setNotificationCode(String notificationCode) { this.notificationCode = notificationCode; }

    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTargetEntityType() { return targetEntityType; }
    public void setTargetEntityType(String targetEntityType) { this.targetEntityType = targetEntityType; }

    public Long getTargetEntityId() { return targetEntityId; }
    public void setTargetEntityId(Long targetEntityId) { this.targetEntityId = targetEntityId; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
}
