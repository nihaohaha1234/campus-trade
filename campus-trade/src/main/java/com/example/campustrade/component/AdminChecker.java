package com.example.campustrade.component;

import com.example.campustrade.common.AuthContext;
import com.example.campustrade.common.BusinessException;
import com.example.campustrade.entity.UserDO;
import com.example.campustrade.enums.UserRole;
import com.example.campustrade.enums.UserStatus;
import com.example.campustrade.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class AdminChecker {

    private final UserMapper userMapper;

    public AdminChecker(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void checkAdmin(){
        Long userId = AuthContext.getCurrentUserId();
        UserDO userDO = userMapper.selectById(userId);
        if(userDO == null){
            throw new BusinessException("用户不存在");
        }
        if(!UserStatus.NORMAL.getCode().equals(userDO.getStatus()) ){
            throw new BusinessException("用户已封禁");
        }
        if(!UserRole.ADMIN.getCode().equals(userDO.getRole())){
            throw new BusinessException("用户没有管理员权限");
        }
    }

}
