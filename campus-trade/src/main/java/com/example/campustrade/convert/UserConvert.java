package com.example.campustrade.convert;

import com.example.campustrade.entity.UserDO;
import com.example.campustrade.vo.UserVO;

//User的do vo转化器
public class UserConvert {
    public static UserVO convertToVO(UserDO userDO){
        UserVO userVO = new UserVO();
        userVO.setUsername(userDO.getUsername());
        userVO.setId(userDO.getId());
        userVO.setNickname(userDO.getNickname());
        userVO.setRole(userDO.getRole());
        userVO.setStatus(userDO.getStatus());
        userVO.setCreateTime(userDO.getCreateTime());
        return userVO;
    }
}
