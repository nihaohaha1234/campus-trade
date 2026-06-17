package com.example.campustrade.vo;

import lombok.Data;

@Data
public class ProductAIReviewVO {

    private String suggestion;

    private String reason;

    public ProductAIReviewVO(){
    }

    public ProductAIReviewVO(String suggestion,String reason){
        this.suggestion = suggestion;
        this.reason = reason;
    }
}
