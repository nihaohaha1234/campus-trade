package com.example.campustrade.service;

import com.example.campustrade.dto.ProductAIOptimizeDTO;
import com.example.campustrade.vo.ProductAIOptimizeVO;

public interface AIService {
    ProductAIOptimizeVO optimizeProduct(ProductAIOptimizeDTO dto);//ai优化商品标题及描述
}
