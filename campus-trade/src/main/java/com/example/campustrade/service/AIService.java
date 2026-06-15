package com.example.campustrade.service;

import com.example.campustrade.dto.ProductAIDTO;
import com.example.campustrade.vo.ProductAIOptimizeVO;
import com.example.campustrade.vo.ProductAIReviewVO;

public interface AIService {
    ProductAIOptimizeVO optimizeProduct(ProductAIDTO dto);//ai优化商品标题及描述

    ProductAIReviewVO reviewProduct(ProductAIDTO dto);//ai审核商品
}
