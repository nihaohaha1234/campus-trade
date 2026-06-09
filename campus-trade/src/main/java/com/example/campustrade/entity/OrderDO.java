package com.example.campustrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class OrderDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long buyerId;

    private Long sellerId;

    private Long productId;

    private BigDecimal price;

    private Integer status; //0待确认 1已确认 2已完成 3已取消

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
