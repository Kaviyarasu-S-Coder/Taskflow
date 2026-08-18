package com.taskflow.modules.project.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "project_members",
    uniqueConstraints = @UniqueConstraint(name = "uq_project_members", columnNames = {"project_id", "user_id"}))
public class ProjectMemberEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pm_project"))
    private ProjectEntity project;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "project_role", nullable = false, length = 30)
    private String projectRole = "MEMBER";

    public ProjectMemberEntity() {}

    public ProjectMemberEntity(ProjectEntity project, Long userId, String projectRole) {
        this.project = project;
        this.userId = userId;
        this.projectRole = projectRole;
    }

    public ProjectEntity getProject() { return project; }
    public void setProject(ProjectEntity project) { this.project = project; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getProjectRole() { return projectRole; }
    public void setProjectRole(String projectRole) { this.projectRole = projectRole; }
}
