package com.example.campustrade.common;


//建一个进程保存token解析出来的userid
public class UserContext {

    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

    public static void setUserId(Long userId){
        USER_ID_HOLDER.set(userId);
    }

    public static Long getUserId(){
        return USER_ID_HOLDER.get();
    }

    public static void clear(){
        USER_ID_HOLDER.remove();
    }
}
