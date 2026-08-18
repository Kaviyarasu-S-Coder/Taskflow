package com.taskflow.modules.task.domain;

public class TaskDependency {

    private Long id;
    private Long predecessorId;
    private Long successorId;
    private String dependencyType;

    public static TaskDependency create(Long predecessorId, Long successorId, String dependencyType) {
        TaskDependency td = new TaskDependency();
        td.predecessorId = predecessorId;
        td.successorId = successorId;
        td.dependencyType = dependencyType != null ? dependencyType : "BLOCKS";
        return td;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPredecessorId() { return predecessorId; }
    public void setPredecessorId(Long predecessorId) { this.predecessorId = predecessorId; }

    public Long getSuccessorId() { return successorId; }
    public void setSuccessorId(Long successorId) { this.successorId = successorId; }

    public String getDependencyType() { return dependencyType; }
    public void setDependencyType(String dependencyType) { this.dependencyType = dependencyType; }
}
