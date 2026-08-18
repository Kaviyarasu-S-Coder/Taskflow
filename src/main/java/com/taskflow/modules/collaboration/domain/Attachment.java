package com.taskflow.modules.collaboration.domain;

import java.util.UUID;

public class Attachment {

    private Long id;
    private String attachmentCode;
    private Long taskId;
    private Long uploaderId;
    private String originalFilename;
    private Long fileSizeBytes;
    private String mimeType;
    private String storagePath;

    public static Attachment create(Long taskId, Long uploaderId, String originalFilename, Long fileSizeBytes, String mimeType, String storagePath) {
        Attachment a = new Attachment();
        a.attachmentCode = "ATT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        a.taskId = taskId;
        a.uploaderId = uploaderId;
        a.originalFilename = originalFilename;
        a.fileSizeBytes = fileSizeBytes;
        a.mimeType = mimeType;
        a.storagePath = storagePath;
        return a;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
