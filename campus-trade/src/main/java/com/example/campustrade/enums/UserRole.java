package com.example.campustrade.enums;

public enum UserRole {
    NORMAL_USER(0,"普通用户"),
    ADMIN(1,"管理员");

    private final Integer code;
    private final String desc;

    UserRole(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
