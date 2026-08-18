package com.taskflow.modules.notification.adapter.in.web;

import com.taskflow.modules.notification.application.port.in.SendNotificationCommand;
import com.taskflow.modules.notification.application.port.in.SendNotificationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SendNotificationUseCase sendNotificationUseCase;

    @Test
    @WithMockUser
    @DisplayName("Should retrieve notifications for user and mark as read")
    void getAndMarkNotificationAsRead() throws Exception {
        SendNotificationCommand cmd = new SendNotificationCommand(
                55L,
                "New Task Assigned",
                "You have been assigned to task CORE-10",
                "TASK",
                10L
        );
        var notifResp = sendNotificationUseCase.sendNotification(cmd);

        mockMvc.perform(get("/api/v1/notifications/user/55"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].isRead", is(false)));

        mockMvc.perform(put("/api/v1/notifications/" + notifResp.id() + "/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRead", is(true)));
    }
}
