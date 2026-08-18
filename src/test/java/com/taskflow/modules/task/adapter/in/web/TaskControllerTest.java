package com.taskflow.modules.task.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.modules.project.adapter.in.web.ProjectRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("Should create tasks, update status, and manage dependencies")
    void taskLifecycleAndDependencies() throws Exception {
        // Create project first
        ProjectRequest projReq = new ProjectRequest(10L, "TSKPROJ", "Task Test Project", "Desc", 1L);
        String projResp = mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projReq)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        Long projectId = objectMapper.readTree(projResp).get("id").asLong();

        // Create Task 1
        TaskRequest t1Req = new TaskRequest(projectId, "First Task", "Description 1", "HIGH", "STORY", 5, BigDecimal.valueOf(8), null, 1L, 2L, null);
        String t1Resp = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(t1Req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.formattedTaskKey", is("TSKPROJ-1")))
                .andReturn().getResponse().getContentAsString();
        Long t1Id = objectMapper.readTree(t1Resp).get("id").asLong();

        // Create Task 2
        TaskRequest t2Req = new TaskRequest(projectId, "Second Task", "Description 2", "MEDIUM", "TASK", 3, BigDecimal.valueOf(4), null, 1L, 2L, null);
        String t2Resp = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(t2Req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.formattedTaskKey", is("TSKPROJ-2")))
                .andReturn().getResponse().getContentAsString();
        Long t2Id = objectMapper.readTree(t2Resp).get("id").asLong();

        // Task 2 depends on Task 1 (t1 blocks t2)
        AddDependencyRequest depReq = new AddDependencyRequest(t1Id, "BLOCKS");
        mockMvc.perform(post("/api/v1/tasks/" + t2Id + "/dependencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dependencies", hasSize(1)));

        // Update Task 1 status to IN_PROGRESS
        UpdateTaskRequest updateReq = new UpdateTaskRequest(null, null, "IN_PROGRESS", null, null, null, null, BigDecimal.valueOf(2), null, null);
        mockMvc.perform(put("/api/v1/tasks/" + t1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.loggedHours", is(2)));
    }
}
