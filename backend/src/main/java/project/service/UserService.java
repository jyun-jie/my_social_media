package project.service;


import project.Dto.LoginRequest;
import project.Dto.RegisterRequest;
import project.entity.User;

public interface UserService {
    void registUser(RegisterRequest userRequest);

    String login(LoginRequest loginRequest);
}
