package com.taskflow.modules.notification.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByRecipientIdAndDeletedFalseOrderByCreatedAtDesc(Long recipientId);
}
