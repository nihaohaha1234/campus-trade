package com.example.campustrade.common;

public class PageParamChecker {

    public static void check(Long page,Long pageSize){

        if(page == null || page < 1){
            throw new BusinessException("页码不能小于1");
        }
        if (pageSize == null || pageSize < 1 || pageSize > 50){
            throw new BusinessException("每页数量必须在1到50之内");
        }
    }
}
