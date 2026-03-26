package project.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import project.Dto.LoginRequest;
import project.Dto.RegisterRequest;
import project.common.Result;
import project.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    @Autowired
    private UserService userService;

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
}
