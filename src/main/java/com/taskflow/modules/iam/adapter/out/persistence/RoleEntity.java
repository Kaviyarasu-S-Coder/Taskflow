package com.taskflow.modules.iam.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class RoleEntity extends BaseAuditEntity {

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
