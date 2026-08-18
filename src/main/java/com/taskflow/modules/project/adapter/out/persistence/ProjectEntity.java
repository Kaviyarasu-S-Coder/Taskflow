package com.taskflow.modules.project.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects",
    uniqueConstraints = @UniqueConstraint(name = "uq_projects_wks_prefix", columnNames = {"workspace_id", "key_prefix"}))
public class ProjectEntity extends BaseAuditEntity {

    @Column(name = "project_code", nullable = false, unique = true, length = 20)
    private String projectCode;

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;

    @Column(name = "key_prefix", nullable = false, length = 10)
    private String keyPrefix;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "lead_id", nullable = false)
    private Long leadId;

    @Column(name = "next_task_seq", nullable = false)
    private Long nextTaskSeq = 1L;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProjectMemberEntity> members = new ArrayList<>();

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }

    public String getKeyPrefix() { return keyPrefix; }
    public void setKeyPrefix(String keyPrefix) { this.keyPrefix = keyPrefix; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public Long getNextTaskSeq() { return nextTaskSeq; }
    public void setNextTaskSeq(Long nextTaskSeq) { this.nextTaskSeq = nextTaskSeq; }

    public List<ProjectMemberEntity> getMembers() { return members; }
    public void setMembers(List<ProjectMemberEntity> members) { this.members = members; }
}
