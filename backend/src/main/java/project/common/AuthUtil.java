package project.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("使用者未登入");
        }

        String userIdStr = authentication.getName();
        return Long.parseLong(userIdStr);
    }

    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());
    }
}
