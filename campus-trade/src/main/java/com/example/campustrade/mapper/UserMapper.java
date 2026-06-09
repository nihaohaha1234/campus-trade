package com.example.campustrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campustrade.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

//mapper可以实现数据库的几个简单方法
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
