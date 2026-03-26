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
import project.repository.UserRepository;
import project.service.UserService;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository ;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder ;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registUser(RegisterRequest request) {
        log.info("開始處理註冊請求，手機號碼: {}", request.getPhoneNumber());

        if( userRepository.existsByPhoneNumber(request.getPhoneNumber())){
            log.warn("手機號碼已存在，拒絕註冊: {}", request.getPhoneNumber());
            throw new RuntimeException("手機號碼已存在 ! ") ;
        }

        User user = new User();
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        log.info("使用者註冊成功，手機號碼: {}", user.getPhoneNumber());
    }

    @Override
    public String login(LoginRequest request) {
        log.info("開始處理登入請求，手機號碼: {}", request.getPhoneNumber());

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> {
                    log.warn("登入失敗，手機號碼不存在: {}", request.getPhoneNumber());
                    return new RuntimeException("手機號碼或密碼錯誤");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("登入失敗，密碼錯誤，手機號碼: {}", request.getPhoneNumber());
            throw new RuntimeException("手機號碼或密碼錯誤");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getPhoneNumber());
        log.info("使用者登入成功，手機號碼: {}", user.getPhoneNumber());

        return token;
    }
}
