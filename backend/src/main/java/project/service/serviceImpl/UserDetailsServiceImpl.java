package project.service.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.repository.UserRepositoryCustom;

import java.util.Collections;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepositoryCustom userRepositoryCustom;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        project.entity.User user = userRepositoryCustom.findByPhoneNumber(phoneNumber);
        if (user == null) {
            log.warn("找不到使用者，手機號碼: {}", phoneNumber);
            throw new UsernameNotFoundException("找不到此手機號碼的使用者");
        }

        return new User(
                user.getUserId().toString(),
                user.getPassword(),
                Collections.emptyList()
        );
    }

    public UserDetails loadUserById(Long userId) {
        project.entity.User user = userRepositoryCustom.findById(userId);
        if (user == null) {
            log.warn("找不到使用者，ID: {}", userId);
            throw new UsernameNotFoundException("找不到此使用者");
        }

        return new User(
                user.getUserId().toString(),
                user.getPassword(),
                Collections.emptyList()
        );
    }
}
