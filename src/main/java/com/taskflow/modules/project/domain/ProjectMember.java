package com.taskflow.modules.project.domain;

public class ProjectMember {

    private Long id;
    private Long projectId;
    private Long userId;
    private String projectRole;

    public static ProjectMember create(Long userId, String projectRole) {
        ProjectMember pm = new ProjectMember();
        pm.userId = userId;
        pm.projectRole = projectRole;
        return pm;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getProjectRole() { return projectRole; }
    public void setProjectRole(String projectRole) { this.projectRole = projectRole; }
}
