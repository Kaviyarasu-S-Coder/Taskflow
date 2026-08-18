package com.taskflow.modules.notification.adapter.out.persistence;

import com.taskflow.modules.notification.domain.Notification;
import com.taskflow.modules.notification.domain.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NotificationPersistenceAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;

    public NotificationPersistenceAdapter(NotificationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = toEntity(notification);
        NotificationEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Notification> findByRecipientId(Long recipientId) {
        return jpaRepository.findByRecipientIdAndDeletedFalseOrderByCreatedAtDesc(recipientId).stream()
                .map(this::toDomain)
                .toList();
    }

    private NotificationEntity toEntity(Notification domain) {
        NotificationEntity entity = new NotificationEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setNotificationCode(domain.getNotificationCode());
        entity.setRecipientId(domain.getRecipientId());
        entity.setTitle(domain.getTitle());
        entity.setMessage(domain.getMessage());
        entity.setTargetEntityType(domain.getTargetEntityType());
        entity.setTargetEntityId(domain.getTargetEntityId());
        entity.setIsRead(domain.getIsRead());
        return entity;
    }

    private Notification toDomain(NotificationEntity entity) {
        Notification n = new Notification();
        n.setId(entity.getId());
        n.setNotificationCode(entity.getNotificationCode());
        n.setRecipientId(entity.getRecipientId());
        n.setTitle(entity.getTitle());
        n.setMessage(entity.getMessage());
        n.setTargetEntityType(entity.getTargetEntityType());
        n.setTargetEntityId(entity.getTargetEntityId());
        n.setIsRead(entity.getIsRead());
        return n;
    }
}
