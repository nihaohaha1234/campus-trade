package com.example.campustrade.enums;

public enum OrderStatus {

    WAIT_CONFIRM(0,"待确认"),
    CONFIRMED(1,"已确认"),
    FINISHED(2,"已完成"),
    CANCELED(3,"已取消");

    OrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;
    private final String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
