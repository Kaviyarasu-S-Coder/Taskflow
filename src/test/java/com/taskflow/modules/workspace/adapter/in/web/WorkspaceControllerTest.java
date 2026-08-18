package com.taskflow.modules.workspace.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WorkspaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("Should successfully create and retrieve workspace")
    void createAndGetWorkspace() throws Exception {
        WorkspaceRequest request = new WorkspaceRequest(
                100L,
                "Engineering",
                "Engineering workspace for core platform"
        );

        String response = mockMvc.perform(post("/api/v1/workspaces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.workspaceCode", startsWith("WKS-")))
                .andExpect(jsonPath("$.name", is("Engineering")))
                .andReturn().getResponse().getContentAsString();

        Long workspaceId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/v1/workspaces/" + workspaceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Engineering")));

        mockMvc.perform(get("/api/v1/workspaces/org/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }
}
