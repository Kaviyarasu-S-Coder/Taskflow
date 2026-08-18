package com.taskflow.modules.iam.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain aggregate representing a registered user.
 * This is a pure Java class — not a JPA entity.
 */
public class User {

    private Long id;
    private String userCode;
    private Long organizationId;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private UserStatus status;
    private Integer failedLoginAttempts;
    private LocalDateTime lockExpiry;
    private List<String> roles;

    // ── Factory ───────────────────────────────────────────────────────────────

    public static User create(String userCode, Long organizationId, String email,
                               String passwordHash, String firstName, String lastName) {
        User user = new User();
        user.userCode = userCode;
        user.organizationId = organizationId;
        user.email = email;
        user.passwordHash = passwordHash;
        user.firstName = firstName;
        user.lastName = lastName;
        user.status = UserStatus.ACTIVE;
        user.failedLoginAttempts = 0;
        return user;
    }

    // ── Domain Behaviour ──────────────────────────────────────────────────────

    public boolean isActive() {
        return UserStatus.ACTIVE == this.status;
    }

    public boolean isLocked() {
        return lockExpiry != null && lockExpiry.isAfter(LocalDateTime.now());
    }

    public void recordFailedLogin(int maxAttempts, int lockMinutes) {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= maxAttempts) {
            this.lockExpiry = LocalDateTime.now().plusMinutes(lockMinutes);
        }
    }

    public void resetFailedLogins() {
        this.failedLoginAttempts = 0;
        this.lockExpiry = null;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public LocalDateTime getLockExpiry() { return lockExpiry; }
    public void setLockExpiry(LocalDateTime lockExpiry) { this.lockExpiry = lockExpiry; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
