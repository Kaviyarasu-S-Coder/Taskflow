package com.taskflow.modules.iam.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully register a new user and return tokens")
    void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "SecurePassword123!",
                "Acme Corp"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.email", is("jane.doe@example.com")))
                .andExpect(jsonPath("$.firstName", is("Jane")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.roles", hasItem("ROLE_DEVELOPER")));
    }

    @Test
    @DisplayName("Should fail registration with duplicate email")
    void registerDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "John",
                "Smith",
                "john.smith@example.com",
                "Password123!",
                "Test Org"
        );

        // First registration
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Duplicate registration
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Business Rule Violation")))
                .andExpect(jsonPath("$.detail", containsString("already exists")));
    }

    @Test
    @DisplayName("Should successfully login with registered user credentials")
    void loginSuccess() throws Exception {
        RegisterRequest regRequest = new RegisterRequest(
                "Alice",
                "Wonder",
                "alice@example.com",
                "MyPassword123",
                "Wonderland Inc"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest("alice@example.com", "MyPassword123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.email", is("alice@example.com")));
    }

    @Test
    @DisplayName("Should fail login with wrong password")
    void loginBadCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "WrongPass");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should successfully refresh token using refresh token")
    void refreshTokenSuccess() throws Exception {
        RegisterRequest regRequest = new RegisterRequest(
                "Bob",
                "Builder",
                "bob@example.com",
                "FixItRight123",
                "Construction Co"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(responseBody).get("refreshToken").asText();

        RefreshTokenRequest refreshReq = new RefreshTokenRequest(refreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.refreshToken", notNullValue()));
    }
}
