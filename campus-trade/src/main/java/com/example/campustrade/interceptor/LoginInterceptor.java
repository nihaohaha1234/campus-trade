package com.example.campustrade.interceptor;

import com.example.campustrade.common.BusinessException;
import com.example.campustrade.common.UserContext;
import com.example.campustrade.entity.UserDO;
import com.example.campustrade.enums.UserStatus;
import com.example.campustrade.mapper.UserMapper;
import com.example.campustrade.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//拦截器
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final UserMapper userMapper;

    public LoginInterceptor(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    //在controller运行前拦截判断有没有登录
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    //先判断只有/products接口的get方法可以不用登陆通过
        String uri = request.getRequestURI();
        if("OPTIONS".equalsIgnoreCase(request.getMethod())){
            return true;
        }
        if ("GET".equalsIgnoreCase(request.getMethod())
                && uri.startsWith("/products")
                && !uri.startsWith("/products/my")
                && !uri.startsWith("/products/recommend")){

            String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer ")){
                String token = authorization.substring(7);
                try {
                    Long userId = JwtUtils.getUserIdFromToken(token);
                    UserContext.setUserId(userId);
                    UserDO userDO = userMapper.selectById(userId);
                    if (userDO == null){
                        UserContext.clear();
                    } else if (UserStatus.DISABLED.getCode().equals(userDO.getStatus())){
                        UserContext.clear();
                    }
                }catch (Exception e){
                    UserContext.clear();
                }
            }
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")){
            throw new BusinessException("请先登录");
        }
        String token = authorization.substring(7);
        Long userId = null;
        try {
            userId = JwtUtils.getUserIdFromToken(token);
            UserContext.setUserId(userId);
        }catch (Exception e){
            throw new BusinessException("登陆状态无效，请重新登录");
        }
        UserDO userDO = userMapper.selectById(userId);
        if (userDO == null){
            throw new BusinessException("登陆状态无效，请重新登录");
        }
        if(userDO.getStatus().equals(UserStatus.DISABLED.getCode())){
            throw new BusinessException("该用户已被禁用");
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        UserContext.clear();
    }

}
