package com.taskflow.modules.task.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks",
    uniqueConstraints = @UniqueConstraint(name = "uq_tasks_proj_number", columnNames = {"project_id", "task_number"}))
public class TaskEntity extends BaseAuditEntity {

    @Column(name = "task_code", nullable = false, unique = true, length = 30)
    private String taskCode;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "task_number", nullable = false)
    private Long taskNumber;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "BACKLOG";

    @Column(name = "priority", nullable = false, length = 20)
    private String priority = "MEDIUM";

    @Column(name = "task_type", nullable = false, length = 20)
    private String taskType = "TASK";

    @Column(name = "story_points")
    private Integer storyPoints;

    @Column(name = "estimated_hours", precision = 6, scale = 2)
    private BigDecimal estimatedHours;

    @Column(name = "logged_hours", nullable = false, precision = 6, scale = 2)
    private BigDecimal loggedHours = BigDecimal.ZERO;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;

    @Column(name = "assignee_id")
    private Long assigneeId;

    @Column(name = "parent_task_id")
    private Long parentTaskId;

    @OneToMany(mappedBy = "predecessor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TaskDependencyEntity> dependencies = new ArrayList<>();

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

    public List<TaskDependencyEntity> getDependencies() { return dependencies; }
    public void setDependencies(List<TaskDependencyEntity> dependencies) { this.dependencies = dependencies; }
}
