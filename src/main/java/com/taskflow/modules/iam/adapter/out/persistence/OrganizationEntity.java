package com.taskflow.modules.iam.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "organizations")
public class OrganizationEntity extends BaseAuditEntity {

    @Column(name = "organization_code", nullable = false, unique = true, length = 20)
    private String organizationCode;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    public String getOrganizationCode() { return organizationCode; }
    public void setOrganizationCode(String organizationCode) { this.organizationCode = organizationCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
