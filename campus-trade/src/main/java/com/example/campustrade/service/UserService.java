package com.example.campustrade.service;

import com.example.campustrade.vo.LoginVO;
import com.example.campustrade.dto.LoginDTO;
import com.example.campustrade.dto.RegisterDTO;

public interface UserService {

    //注册服务
    void register(RegisterDTO registerDTO);//用户注册

    //登录服务 返回user
    LoginVO login(LoginDTO loginDTO);//用户登录 用redis减少登录访问次数
}
