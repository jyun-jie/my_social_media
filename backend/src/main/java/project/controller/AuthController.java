package project.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.Dto.LoginRequest;
import project.Dto.RegisterRequest;
import project.common.JwtUtil;
import project.common.Result;
import project.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest userRequest) {
        log.info("收到註冊請求");
        userService.registUser(userRequest);
        log.info("註冊流程完成");
        return Result.success("註冊成功！");
    }

    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("收到登入請求");
        String token = userService.login(loginRequest);
        log.info("登入流程完成");
        return Result.success(token);
    }

    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestHeader("Authorization") String authHeader) {
        log.info("收到 Token 刷新請求");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error(400, "無效的 Token");
        }
        String token = authHeader.substring(7);
        try {
            Long userId = jwtUtil.extractUserId(token);
            String phoneNumber = jwtUtil.extractPhoneNumber(token);
            String newToken = jwtUtil.generateToken(userId, phoneNumber);
            log.info("Token 刷新成功");
            return Result.success(newToken);
        } catch (Exception e) {
            log.warn("Token 刷新失敗: {}", e.getMessage());
            return Result.error(401, "Token 已過期，請重新登入");
        }
    }
}
