package com.example.campustrade.common;


public class AuthContext {

    public static Long getCurrentUserId(){
        Long userId = UserContext.getUserId();
        if (userId == null){
            throw new BusinessException("请重新登录");
        }
        return userId;
    }
}
