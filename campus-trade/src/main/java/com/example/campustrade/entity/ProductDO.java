package com.example.campustrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class ProductDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String description;

    private BigDecimal price;

    private Integer status;//0在审核 1已上架 2已下架 3已售出 4已锁定

    private String imageUrl;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
