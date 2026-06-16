package com.example.campustrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AIReviewLogVO {

    private Long id;

    private Long userId;

    private String productTitle;

    private String productDescription;

    private BigDecimal productPrice;

    private String suggestion;

    private String reason;

    private LocalDateTime createTime;
}
