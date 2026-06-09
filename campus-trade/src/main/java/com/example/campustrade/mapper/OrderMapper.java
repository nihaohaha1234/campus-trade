package com.example.campustrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.campustrade.entity.OrderDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderDO> {
}
