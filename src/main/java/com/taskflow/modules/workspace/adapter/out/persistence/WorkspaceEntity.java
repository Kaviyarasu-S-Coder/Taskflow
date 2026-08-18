package com.taskflow.modules.workspace.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "workspaces")
public class WorkspaceEntity extends BaseAuditEntity {

    @Column(name = "workspace_code", nullable = false, unique = true, length = 20)
    private String workspaceCode;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public String getWorkspaceCode() { return workspaceCode; }
    public void setWorkspaceCode(String workspaceCode) { this.workspaceCode = workspaceCode; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
