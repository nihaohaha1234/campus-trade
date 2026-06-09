package com.example.campustrade.enums;

public enum UserStatus {
    DISABLED(0,"禁用"),
    NORMAL(1,"正常");

    private final Integer code;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    private final String desc;

    UserStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
