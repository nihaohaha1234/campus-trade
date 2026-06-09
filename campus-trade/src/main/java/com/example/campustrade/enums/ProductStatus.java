package com.example.campustrade.enums;

public enum ProductStatus {

    PENDING_REVIEW(0,"待审核"),
    ON_SALE(1,"已上架"),
    OFF_SHELF(2,"已下架/审核拒绝"),
    SOLD(3,"已售出"),
    LOCKED(4,"已锁定");

    private final Integer code;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    private final String desc;

    ProductStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
