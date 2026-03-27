package project.service.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.Dto.LoginRequest;
import project.Dto.RegisterRequest;
import project.common.JwtUtil;
import project.entity.User;
import project.repository.UserRepositoryCustom;
import project.service.UserService;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepositoryCustom userRepositoryCustom;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registUser(RegisterRequest request) {
        log.info("開始處理註冊請求，手機號碼: {}", request.getPhoneNumber());

        User existingUser = userRepositoryCustom.findByPhoneNumber(request.getPhoneNumber());
        if (existingUser != null) {
            log.warn("手機號碼已存在，拒絕註冊: {}", request.getPhoneNumber());
            throw new RuntimeException("手機號碼已存在 ! ");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        userRepositoryCustom.registerUser(
                request.getUserName(),
                request.getPhoneNumber(),
                request.getEmail(),
                encodedPassword
        );

        log.info("使用者註冊成功，手機號碼: {}", request.getPhoneNumber());
    }

    @Override
    public String login(LoginRequest request) {
        log.info("開始處理登入請求，手機號碼: {}", request.getPhoneNumber());

        User user = userRepositoryCustom.findByPhoneNumber(request.getPhoneNumber());
        if (user == null) {
            log.warn("登入失敗，手機號碼不存在: {}", request.getPhoneNumber());
            throw new RuntimeException("手機號碼或密碼錯誤");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("登入失敗，密碼錯誤，手機號碼: {}", request.getPhoneNumber());
            throw new RuntimeException("手機號碼或密碼錯誤");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getPhoneNumber());
        log.info("使用者登入成功，手機號碼: {}", user.getPhoneNumber());

        return token;
    }
}
