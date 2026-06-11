package com.example.campustrade.controller;


import com.example.campustrade.common.Result;
import com.example.campustrade.dto.ProductAIOptimizeDTO;
import com.example.campustrade.service.AIService;
import com.example.campustrade.vo.ProductAIOptimizeVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/products/optimize")
    public Result<ProductAIOptimizeVO> optimizeProduct(@RequestBody ProductAIOptimizeDTO dto){
        return Result.success(aiService.optimizeProduct(dto));
    }
}
