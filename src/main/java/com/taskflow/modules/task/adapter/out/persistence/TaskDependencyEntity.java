package com.taskflow.modules.task.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "task_dependencies",
    uniqueConstraints = @UniqueConstraint(name = "uq_task_dep", columnNames = {"predecessor_id", "successor_id"}))
public class TaskDependencyEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predecessor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_td_predecessor"))
    private TaskEntity predecessor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "successor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_td_successor"))
    private TaskEntity successor;

    @Column(name = "dependency_type", nullable = false, length = 20)
    private String dependencyType = "BLOCKS";

    public TaskDependencyEntity() {}

    public TaskDependencyEntity(TaskEntity predecessor, TaskEntity successor, String dependencyType) {
        this.predecessor = predecessor;
        this.successor = successor;
        this.dependencyType = dependencyType;
    }

    public TaskEntity getPredecessor() { return predecessor; }
    public void setPredecessor(TaskEntity predecessor) { this.predecessor = predecessor; }

    public TaskEntity getSuccessor() { return successor; }
    public void setSuccessor(TaskEntity successor) { this.successor = successor; }

    public String getDependencyType() { return dependencyType; }
    public void setDependencyType(String dependencyType) { this.dependencyType = dependencyType; }
}
