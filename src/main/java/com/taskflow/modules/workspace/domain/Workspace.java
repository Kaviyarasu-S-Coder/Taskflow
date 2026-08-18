package com.taskflow.modules.workspace.domain;

import java.util.UUID;

public class Workspace {

    private Long id;
    private String workspaceCode;
    private Long organizationId;
    private String name;
    private String description;

    public static Workspace create(Long organizationId, String name, String description) {
        Workspace w = new Workspace();
        w.workspaceCode = "WKS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        w.organizationId = organizationId;
        w.name = name;
        w.description = description;
        return w;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWorkspaceCode() { return workspaceCode; }
    public void setWorkspaceCode(String workspaceCode) { this.workspaceCode = workspaceCode; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
