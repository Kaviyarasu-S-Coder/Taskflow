package com.taskflow.modules.collaboration.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentJpaRepository extends JpaRepository<AttachmentEntity, Long> {

    List<AttachmentEntity> findByTaskIdAndDeletedFalse(Long taskId);
}
