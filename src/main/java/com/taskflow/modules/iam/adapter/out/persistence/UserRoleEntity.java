package com.taskflow.modules.iam.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "user_roles",
    uniqueConstraints = @UniqueConstraint(name = "uq_user_roles", columnNames = {"user_id", "role_id"}))
public class UserRoleEntity extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_ur_user"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_ur_role"))
    private RoleEntity role;

    public UserRoleEntity() {}

    public UserRoleEntity(UserEntity user, RoleEntity role) {
        this.user = user;
        this.role = role;
    }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public RoleEntity getRole() { return role; }
    public void setRole(RoleEntity role) { this.role = role; }
}
