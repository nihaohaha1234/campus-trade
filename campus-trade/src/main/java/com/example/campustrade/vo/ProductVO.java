package com.example.campustrade.vo;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {

    private Long id;

    private Long userId;

    private String title;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    private Integer status;
}
