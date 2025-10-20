package br.com.rastrodeliberdade.auth_service.controller;

import br.com.rastrodeliberdade.auth_service.dto.LoginRequestDto;
import br.com.rastrodeliberdade.auth_service.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenService tokenService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = (User) User.builder()
                .username("user.test@rastro.com")
                .password(new BCryptPasswordEncoder().encode("password123"))
                .roles("USER")
                .build();
    }


    @Test
    @DisplayName("Integration Test: Should return 200 OK and a JWT when login is successful")
    void login_withValidCredentials_shouldReturnToken() throws Exception {

        when(userDetailsService.loadUserByUsername("user.test@rastro.com")).thenReturn(testUser);

        LoginRequestDto loginRequest = new LoginRequestDto("user.test@rastro.com", "password123");

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("Integration Test: Should return 403 Forbidden for incorrect password")
    void login_withIncorrectPassword_shouldReturnForbidden() throws Exception {
        when(userDetailsService.loadUserByUsername("user.test@rastro.com")).thenReturn(testUser);

        LoginRequestDto loginRequest = new LoginRequestDto("user.test@rastro.com", "wrong-password");

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Integration Test: Should return 403 Forbidden for non-existent user")
    void login_withNonExistentUser_shouldReturnForbidden() throws Exception {
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(null);

        LoginRequestDto loginRequest = new LoginRequestDto("ghost@rastro.com", "any-password");

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden());
    }
}