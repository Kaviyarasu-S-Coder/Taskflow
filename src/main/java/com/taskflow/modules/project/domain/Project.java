package com.taskflow.modules.project.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project {

    private Long id;
    private String projectCode;
    private Long workspaceId;
    private String keyPrefix;
    private String name;
    private String description;
    private String status;
    private Long leadId;
    private Long nextTaskSeq;
    private List<ProjectMember> members = new ArrayList<>();

    public static Project create(Long workspaceId, String keyPrefix, String name, String description, Long leadId) {
        Project p = new Project();
        p.projectCode = "PRJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        p.workspaceId = workspaceId;
        p.keyPrefix = keyPrefix.toUpperCase();
        p.name = name;
        p.description = description;
        p.status = "ACTIVE";
        p.leadId = leadId;
        p.nextTaskSeq = 1L;
        // Project Lead is automatically added as a LEAD member
        p.members.add(ProjectMember.create(leadId, "LEAD"));
        return p;
    }

    public Long incrementAndGetNextTaskSeq() {
        Long current = this.nextTaskSeq;
        this.nextTaskSeq++;
        return current;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public List<ProjectMember> getMembers() { return members; }
    public void setMembers(List<ProjectMember> members) { this.members = members; }
}
