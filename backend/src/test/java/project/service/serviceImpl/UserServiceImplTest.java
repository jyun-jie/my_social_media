package project.service.serviceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import project.Dto.LoginRequest;
import project.Dto.RegisterRequest;
import project.common.JwtUtil;
import project.entity.User;
import project.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registUser_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0912345678");
        request.setPassword("password123");
        request.setUserName("testUser");
        request.setEmail("test@example.com");

        when(userRepository.existsByPhoneNumber("0912345678")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // Act
        userService.registUser(request);

        // Assert
        verify(userRepository).existsByPhoneNumber("0912345678");
        verify(passwordEncoder).encode("password123");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("0912345678", savedUser.getPhoneNumber());
        assertEquals("testUser", savedUser.getUserName());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("hashedPassword", savedUser.getPassword());
    }

    @Test
    void registUser_PhoneNumberAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0912345678");
        request.setPassword("password123");
        request.setUserName("testUser");
        request.setEmail("test@example.com");

        when(userRepository.existsByPhoneNumber("0912345678")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registUser(request);
        });

        assertEquals("手機號碼已存在 ! ", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registUser_PasswordEncrypted() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNumber("0987654321");
        request.setPassword("mySecretPass");
        request.setUserName("anotherUser");
        request.setEmail("another@example.com");

        when(userRepository.existsByPhoneNumber("0987654321")).thenReturn(false);
        when(passwordEncoder.encode("mySecretPass")).thenReturn("$2a$10$hashedValue");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // Act
        userService.registUser(request);

        // Assert
        verify(passwordEncoder).encode("mySecretPass");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("$2a$10$hashedValue", savedUser.getPassword());
        assertNotEquals("mySecretPass", savedUser.getPassword());
    }

    @Test
    void login_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setPhoneNumber("0912345678");
        request.setPassword("password123");

        User user = new User();
        user.setUserId(1L);
        user.setPhoneNumber("0912345678");
        user.setPassword("hashedPassword");

        when(userRepository.findByPhoneNumber("0912345678")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "0912345678")).thenReturn("jwtToken123");

        // Act
        String token = userService.login(request);

        // Assert
        assertEquals("jwtToken123", token);
        verify(userRepository).findByPhoneNumber("0912345678");
        verify(passwordEncoder).matches("password123", "hashedPassword");
        verify(jwtUtil).generateToken(1L, "0912345678");
    }

    @Test
    void login_PhoneNumberNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setPhoneNumber("0912345678");
        request.setPassword("password123");

        when(userRepository.findByPhoneNumber("0912345678")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });

        assertEquals("手機號碼或密碼錯誤", exception.getMessage());
        verify(jwtUtil, never()).generateToken(anyLong(), anyString());
    }

    @Test
    void login_WrongPassword() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setPhoneNumber("0912345678");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setUserId(1L);
        user.setPhoneNumber("0912345678");
        user.setPassword("hashedPassword");

        when(userRepository.findByPhoneNumber("0912345678")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });

        assertEquals("手機號碼或密碼錯誤", exception.getMessage());
        verify(jwtUtil, never()).generateToken(anyLong(), anyString());
    }
}
