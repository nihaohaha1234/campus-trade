package com.example.campustrade.controller;


import com.example.campustrade.common.Result;
import com.example.campustrade.dto.ProductAIDTO;
import com.example.campustrade.service.AIService;
import com.example.campustrade.vo.AIReviewLogVO;
import com.example.campustrade.vo.PageVO;
import com.example.campustrade.vo.ProductAIOptimizeVO;
import com.example.campustrade.vo.ProductAIReviewVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/products/optimize")
    public Result<ProductAIOptimizeVO> optimizeProduct(@RequestBody ProductAIDTO dto){
        return Result.success(aiService.optimizeProduct(dto));
    }

    @PostMapping("/products/review")
    public Result<ProductAIReviewVO> reviewProduct(@RequestBody ProductAIDTO dto){
        return Result.success(aiService.reviewProduct(dto));
    }

}
