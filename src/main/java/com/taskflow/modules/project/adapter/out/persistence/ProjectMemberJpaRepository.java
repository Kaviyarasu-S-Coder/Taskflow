package com.taskflow.modules.project.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberJpaRepository extends JpaRepository<ProjectMemberEntity, Long> {

    List<ProjectMemberEntity> findByProjectIdAndDeletedFalse(Long projectId);

    Optional<ProjectMemberEntity> findByProjectIdAndUserIdAndDeletedFalse(Long projectId, Long userId);

    boolean existsByProjectIdAndUserIdAndDeletedFalse(Long projectId, Long userId);
}
