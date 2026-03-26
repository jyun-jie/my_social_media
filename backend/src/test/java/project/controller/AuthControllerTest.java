package project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import project.Dto.LoginRequest;
import project.Dto.RegisterRequest;
import project.service.UserService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    // ===== 註冊測試 =====

    @Test
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0912345678");
        request.setPassword("password123");
        request.setUserName("testUser");
        request.setEmail("test@example.com");

        doNothing().when(userService).registUser(any(RegisterRequest.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("註冊成功！"));

        verify(userService).registUser(any(RegisterRequest.class));
    }

    @Test
    void register_InvalidPhoneNumber() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("1234567890");
        request.setPassword("password123");
        request.setUserName("testUser");
        request.setEmail("test@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        verify(userService, never()).registUser(any(RegisterRequest.class));
    }

    @Test
    void register_PasswordTooShort() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0912345678");
        request.setPassword("1234567");
        request.setUserName("testUser");
        request.setEmail("test@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        verify(userService, never()).registUser(any(RegisterRequest.class));
    }

    @Test
    void register_InvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0912345678");
        request.setPassword("password123");
        request.setUserName("testUser");
        request.setEmail("invalidEmail");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        verify(userService, never()).registUser(any(RegisterRequest.class));
    }

    @Test
    void register_EmptyPhoneNumber() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("");
        request.setPassword("password123");
        request.setUserName("testUser");
        request.setEmail("test@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        verify(userService, never()).registUser(any(RegisterRequest.class));
    }

    // ===== 登入測試 =====

    @Test
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPhoneNumber("0912345678");
        request.setPassword("password123");

        when(userService.login(any(LoginRequest.class))).thenReturn("jwtToken123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("jwtToken123"));

        verify(userService).login(any(LoginRequest.class));
    }

    @Test
    void login_InvalidPhoneNumber() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPhoneNumber("1234567890");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        verify(userService, never()).login(any(LoginRequest.class));
    }

    @Test
    void login_PasswordTooShort() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPhoneNumber("0912345678");
        request.setPassword("1234567");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        verify(userService, never()).login(any(LoginRequest.class));
    }

    @Test
    void login_EmptyPhoneNumber() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPhoneNumber("");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));

        verify(userService, never()).login(any(LoginRequest.class));
    }
}
