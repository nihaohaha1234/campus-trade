package com.example.campustrade.controller;

import com.example.campustrade.vo.LoginVO;
import com.example.campustrade.common.Result;
import com.example.campustrade.dto.LoginDTO;
import com.example.campustrade.dto.RegisterDTO;
import com.example.campustrade.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterDTO registerDTO){
        userService.register(registerDTO);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO){
        LoginVO loginVO= userService.login(loginDTO);
        return Result.success(loginVO);
    }
}
