package com.example.campustrade.vo;

import lombok.Data;

import java.time.LocalDateTime;

//后端返回给前端的信息 前端不给看用户密码
@Data
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private Integer role;

    private Integer status;

    private LocalDateTime createTime;
}
