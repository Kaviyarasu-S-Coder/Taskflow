package com.taskflow.modules.task.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Task {

    private Long id;
    private String taskCode;
    private Long projectId;
    private Long taskNumber;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String taskType;
    private Integer storyPoints;
    private BigDecimal estimatedHours;
    private BigDecimal loggedHours;
    private LocalDateTime dueDate;
    private Long reporterId;
    private Long assigneeId;
    private Long parentTaskId;
    private List<TaskDependency> dependencies = new ArrayList<>();

    public static Task create(Long projectId, Long taskNumber, String title, String description,
                               String priority, String taskType, Integer storyPoints,
                               BigDecimal estimatedHours, LocalDateTime dueDate,
                               Long reporterId, Long assigneeId, Long parentTaskId) {
        Task t = new Task();
        t.taskCode = "TSK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        t.projectId = projectId;
        t.taskNumber = taskNumber;
        t.title = title;
        t.description = description;
        t.status = "BACKLOG";
        t.priority = priority != null ? priority : "MEDIUM";
        t.taskType = taskType != null ? taskType : "TASK";
        t.storyPoints = storyPoints;
        t.estimatedHours = estimatedHours;
        t.loggedHours = BigDecimal.ZERO;
        t.dueDate = dueDate;
        t.reporterId = reporterId;
        t.assigneeId = assigneeId;
        t.parentTaskId = parentTaskId;
        return t;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTaskCode() { return taskCode; }
    public void setTaskCode(String taskCode) { this.taskCode = taskCode; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getTaskNumber() { return taskNumber; }
    public void setTaskNumber(Long taskNumber) { this.taskNumber = taskNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public Integer getStoryPoints() { return storyPoints; }
    public void setStoryPoints(Integer storyPoints) { this.storyPoints = storyPoints; }

    public BigDecimal getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(BigDecimal estimatedHours) { this.estimatedHours = estimatedHours; }

    public BigDecimal getLoggedHours() { return loggedHours; }
    public void setLoggedHours(BigDecimal loggedHours) { this.loggedHours = loggedHours; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public Long getReporterId() { return reporterId; }
    public void setReporterId(Long reporterId) { this.reporterId = reporterId; }

    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }

    public Long getParentTaskId() { return parentTaskId; }
    public void setParentTaskId(Long parentTaskId) { this.parentTaskId = parentTaskId; }

    public List<TaskDependency> getDependencies() { return dependencies; }
    public void setDependencies(List<TaskDependency> dependencies) { this.dependencies = dependencies; }
}
