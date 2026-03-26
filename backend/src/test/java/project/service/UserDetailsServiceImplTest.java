package project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import project.entity.User;
import project.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_Success() {
        // Arrange
        String phoneNumber = "0912345678";
        User user = new User();
        user.setUserId(1L);
        user.setPhoneNumber(phoneNumber);
        user.setPassword("hashedPassword");

        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);

        // Assert
        assertNotNull(userDetails);
        assertEquals("1", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());

        verify(userRepository).findByPhoneNumber(phoneNumber);
    }

    @Test
    void loadUserByUsername_UserNotFound_ShouldThrowException() {
        // Arrange
        String phoneNumber = "0999999999";
        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(phoneNumber));

        verify(userRepository).findByPhoneNumber(phoneNumber);
    }

    @Test
    void loadUserById_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setPhoneNumber("0912345678");
        user.setPassword("hashedPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserById(userId);

        // Assert
        assertNotNull(userDetails);
        assertEquals("1", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());

        verify(userRepository).findById(userId);
    }

    @Test
    void loadUserById_UserNotFound_ShouldThrowException() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserById(userId));

        verify(userRepository).findById(userId);
    }
}
