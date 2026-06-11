package com.example.campustrade.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {

    //@NotNull(message = "用户id不能为空")
    //private Long userId; 用户id先临时传入后端 等用jwt之后再写直接传入的

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "商品描述不能为空")
    private String description;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01",message = "价格必须大于0")
    @DecimalMax(value = "9999999",message = "价格不能过大")
    private BigDecimal price;

    private String imageUrl;

}
