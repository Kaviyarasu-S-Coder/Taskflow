package com.taskflow.modules.iam.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity extends BaseAuditEntity {

    @Column(name = "user_code", nullable = false, unique = true, length = 20)
    private String userCode;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    @Column(name = "lock_expiry")
    private LocalDateTime lockExpiry;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserRoleEntity> userRoles = new ArrayList<>();

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public String getUserCode() { return userCode; }
    public void setUserCode(String userCode) { this.userCode = userCode; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public LocalDateTime getLockExpiry() { return lockExpiry; }
    public void setLockExpiry(LocalDateTime lockExpiry) { this.lockExpiry = lockExpiry; }

    public List<UserRoleEntity> getUserRoles() { return userRoles; }
    public void setUserRoles(List<UserRoleEntity> userRoles) { this.userRoles = userRoles; }
}
