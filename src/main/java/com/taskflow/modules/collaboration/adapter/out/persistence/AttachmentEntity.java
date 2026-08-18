package com.taskflow.modules.collaboration.adapter.out.persistence;

import com.taskflow.common.domain.BaseAuditEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "attachments")
public class AttachmentEntity extends BaseAuditEntity {

    @Column(name = "attachment_code", nullable = false, unique = true, length = 30)
    private String attachmentCode;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    public String getAttachmentCode() { return attachmentCode; }
    public void setAttachmentCode(String attachmentCode) { this.attachmentCode = attachmentCode; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getUploaderId() { return uploaderId; }
    public void setUploaderId(Long uploaderId) { this.uploaderId = uploaderId; }

    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }

    public Long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(Long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
}
