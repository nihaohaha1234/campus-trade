package com.example.campustrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class UserDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private Integer role;   //0为普通用户 1为管理员

    private Integer status;//0为封禁 1为正常使用

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
