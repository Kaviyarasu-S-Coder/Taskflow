package com.taskflow.modules.notification.domain;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(Long id);

    List<Notification> findByRecipientId(Long recipientId);
}
