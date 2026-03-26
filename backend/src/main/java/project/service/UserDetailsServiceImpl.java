package project.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.repository.UserRepository;

import java.util.Collections;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        project.entity.User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> {
                    log.warn("找不到使用者，手機號碼: {}", phoneNumber);
                    return new UsernameNotFoundException("找不到此手機號碼的使用者");
                });

        return new User(
                user.getUserId().toString(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    public UserDetails loadUserById(Long userId) {
        project.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("找不到使用者，ID: {}", userId);
                    return new UsernameNotFoundException("找不到此使用者");
                });

        return new User(
                user.getUserId().toString(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
