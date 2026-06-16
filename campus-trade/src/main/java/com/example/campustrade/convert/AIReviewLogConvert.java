package com.example.campustrade.convert;

import com.example.campustrade.dto.ProductAIDTO;
import com.example.campustrade.entity.AIReviewLogDO;
import com.example.campustrade.vo.AIReviewLogVO;
import com.example.campustrade.vo.ProductAIReviewVO;

public class AIReviewLogConvert {

    public static AIReviewLogVO convertToVO(AIReviewLogDO aiReviewLogDO){
        AIReviewLogVO aiReviewLogVO = new AIReviewLogVO();
        aiReviewLogVO.setId(aiReviewLogDO.getId());
        aiReviewLogVO.setUserId(aiReviewLogDO.getUserId());
        aiReviewLogVO.setProductTitle(aiReviewLogDO.getProductTitle());
        aiReviewLogVO.setProductDescription(aiReviewLogDO.getProductDescription());
        aiReviewLogVO.setProductPrice(aiReviewLogDO.getProductPrice());
        aiReviewLogVO.setSuggestion(aiReviewLogDO.getSuggestion());
        aiReviewLogVO.setReason(aiReviewLogDO.getReason());
        aiReviewLogVO.setCreateTime(aiReviewLogDO.getCreateTime());
        return aiReviewLogVO;
    }

    public static AIReviewLogDO convertToDO(ProductAIDTO dto, Long userId, ProductAIReviewVO productAIReviewVO){
        AIReviewLogDO aiReviewLogDO = new AIReviewLogDO();
        aiReviewLogDO.setProductTitle(dto.getTitle());
        aiReviewLogDO.setProductDescription(dto.getDescription());
        aiReviewLogDO.setProductPrice(dto.getPrice());
        aiReviewLogDO.setUserId(userId);
        aiReviewLogDO.setSuggestion(productAIReviewVO.getSuggestion());
        aiReviewLogDO.setReason(productAIReviewVO.getReason());
        return aiReviewLogDO;
    }
}
