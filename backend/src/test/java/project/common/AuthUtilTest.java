package project.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AuthUtilTest {

    private final AuthUtil authUtil = new AuthUtil();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_LoggedIn_ShouldReturnUserId() {
        // Arrange
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        Long userId = authUtil.getCurrentUserId();

        // Assert
        assertEquals(1L, userId);
    }

    @Test
    void getCurrentUserId_NotLoggedIn_ShouldThrowException() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> authUtil.getCurrentUserId());
    }

    @Test
    void isLoggedIn_Authenticated_ShouldReturnTrue() {
        // Arrange
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("1", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        boolean result = authUtil.isLoggedIn();

        // Assert
        assertTrue(result);
    }

    @Test
    void isLoggedIn_NotAuthenticated_ShouldReturnFalse() {
        // Act
        boolean result = authUtil.isLoggedIn();

        // Assert
        assertFalse(result);
    }

    @Test
    void isLoggedIn_AnonymousUser_ShouldReturnFalse() {
        // Arrange
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("anonymousUser", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        boolean result = authUtil.isLoggedIn();

        // Assert
        assertFalse(result);
    }
}
