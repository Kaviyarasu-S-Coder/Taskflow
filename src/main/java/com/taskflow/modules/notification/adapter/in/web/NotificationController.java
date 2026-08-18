package com.taskflow.modules.notification.adapter.in.web;

import com.taskflow.modules.notification.application.dto.NotificationResponse;
import com.taskflow.modules.notification.application.port.in.GetNotificationsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Endpoints for user notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final GetNotificationsUseCase getNotificationsUseCase;

    public NotificationController(GetNotificationsUseCase getNotificationsUseCase) {
        this.getNotificationsUseCase = getNotificationsUseCase;
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications by User ID", description = "Lists all notifications for a specific user ordered by latest first")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(getNotificationsUseCase.getNotificationsByRecipientId(userId));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Updates notification status to read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(getNotificationsUseCase.markAsRead(id));
    }
}
