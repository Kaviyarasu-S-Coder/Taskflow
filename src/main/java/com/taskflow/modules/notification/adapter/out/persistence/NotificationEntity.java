package com.taskflow.modules.notification.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class NotificationEntity extends BaseAuditEntity {

    @Column(name = "notification_code", nullable = false, unique = true, length = 30)
    private String notificationCode;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "target_entity_type", nullable = false, length = 50)
    private String targetEntityType;

    @Column(name = "target_entity_id", nullable = false)
    private Long targetEntityId;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

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
