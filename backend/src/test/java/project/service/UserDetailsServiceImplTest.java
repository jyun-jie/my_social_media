package project.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import project.entity.User;
import project.repository.UserRepositoryCustom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepositoryCustom userRepositoryCustom;

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

        when(userRepositoryCustom.findByPhoneNumber(phoneNumber)).thenReturn(user);

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);

        // Assert
        assertNotNull(userDetails);
        assertEquals("1", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());

        verify(userRepositoryCustom).findByPhoneNumber(phoneNumber);
    }

    @Test
    void loadUserByUsername_UserNotFound_ShouldThrowException() {
        // Arrange
        String phoneNumber = "0999999999";
        when(userRepositoryCustom.findByPhoneNumber(phoneNumber)).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(phoneNumber));

        verify(userRepositoryCustom).findByPhoneNumber(phoneNumber);
    }

    @Test
    void loadUserById_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setUserId(userId);
        user.setPhoneNumber("0912345678");
        user.setPassword("hashedPassword");

        when(userRepositoryCustom.findById(userId)).thenReturn(user);

        // Act
        UserDetails userDetails = userDetailsService.loadUserById(userId);

        // Assert
        assertNotNull(userDetails);
        assertEquals("1", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());

        verify(userRepositoryCustom).findById(userId);
    }

    @Test
    void loadUserById_UserNotFound_ShouldThrowException() {
        // Arrange
        Long userId = 999L;
        when(userRepositoryCustom.findById(userId)).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserById(userId));

        verify(userRepositoryCustom).findById(userId);
    }
}
