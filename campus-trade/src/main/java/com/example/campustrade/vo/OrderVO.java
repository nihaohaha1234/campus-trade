package com.example.campustrade.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderVO {
    private Long id;

    private String orderNo;

    private Long productId;

    private Long buyerId;

    private Long sellerId;

    private String productTitle;

    private BigDecimal price;

    private Integer status;

    private String imageUrl;

    private LocalDateTime createTime;
}
