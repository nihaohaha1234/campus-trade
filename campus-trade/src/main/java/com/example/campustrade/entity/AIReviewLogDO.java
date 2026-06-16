package com.example.campustrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ai_review_log")
public class AIReviewLogDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String productTitle;

    private String productDescription;

    private BigDecimal productPrice;

    private String suggestion;

    private String reason;

    private LocalDateTime createTime;
}
