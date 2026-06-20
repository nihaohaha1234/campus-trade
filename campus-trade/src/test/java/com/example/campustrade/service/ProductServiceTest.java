package com.example.campustrade.service;

import com.example.campustrade.common.BusinessException;
import com.example.campustrade.common.UserContext;
import com.example.campustrade.dto.ProductDTO;
import com.example.campustrade.entity.AIReviewLogDO;
import com.example.campustrade.entity.ProductDO;
import com.example.campustrade.enums.ProductStatus;
import com.example.campustrade.mapper.AIReviewLogMapper;
import com.example.campustrade.mapper.ProductMapper;
import com.example.campustrade.service.impl.ProductServiceImpl;
import com.example.campustrade.vo.ProductAIReviewVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    ProductMapper productMapper;
    @Mock
    AIReviewLogMapper aiReviewLogMapper;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    AIService aiService;
    @Mock
    StringRedisTemplate stringRedisTemplate;

    ProductServiceImpl productService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        productService = new ProductServiceImpl(productMapper,aiReviewLogMapper, stringRedisTemplate, objectMapper, aiService);
    }
    //AI审核降级单测
    @Test
    void AI正常通过(){
        Long userId = 1L;
        UserContext.setUserId(userId);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("测试");
        productDTO.setDescription("测试");
        productDTO.setPrice(new BigDecimal(1));
        when(aiService.reviewProduct(any())).thenReturn(new ProductAIReviewVO("PASS","ai审核通过"));
        productService.productPublish(productDTO);
        ArgumentCaptor<ProductDO> productCaptor = ArgumentCaptor.forClass(ProductDO.class);
        verify(productMapper).insert(productCaptor.capture());
        assertEquals(productCaptor.getValue().getStatus(),ProductStatus.PENDING_REVIEW.getCode());
        verify(aiReviewLogMapper,times(1)).insert(any(AIReviewLogDO.class));
        UserContext.clear();
    }

    @Test
    void AI审核不可用_降级(){
        Long userId = 1L;
        UserContext.setUserId(userId);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("测试");
        productDTO.setDescription("测试");
        productDTO.setPrice(new BigDecimal(1));
        when(aiService.reviewProduct(any())).thenThrow(new RuntimeException("ai超时"));
        productService.productPublish(productDTO);
        ArgumentCaptor<AIReviewLogDO> logCaptor = ArgumentCaptor.forClass(AIReviewLogDO.class);
        ArgumentCaptor<ProductDO> productCaptor = ArgumentCaptor.forClass(ProductDO.class);
        verify(aiReviewLogMapper).insert(logCaptor.capture());
        verify(productMapper).insert(productCaptor.capture());
        assertEquals(productCaptor.getValue().getStatus(),ProductStatus.PENDING_REVIEW.getCode());
        assertTrue(logCaptor.getValue().getReason().contains("AI审核不可用"));
        UserContext.clear();
    }

    @Test
    void AI审核未通过(){
        Long userId = 1L;
        UserContext.setUserId(userId);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setTitle("测试");
        productDTO.setDescription("测试");
        productDTO.setPrice(new BigDecimal(1));
        when(aiService.reviewProduct(any())).thenReturn(new ProductAIReviewVO("REJECT","AI审核未通过"));
        BusinessException exception = assertThrows(BusinessException.class,()->{
            productService.productPublish(productDTO);
        });
        assertTrue(exception.getMessage().contains("AI审核未通过"));
        ArgumentCaptor<AIReviewLogDO> logCaptor = ArgumentCaptor.forClass(AIReviewLogDO.class);
        ArgumentCaptor<ProductDO> productCaptor = ArgumentCaptor.forClass(ProductDO.class);
        verify(aiReviewLogMapper).insert(logCaptor.capture());
        verify(productMapper).insert(productCaptor.capture());
        assertEquals(logCaptor.getValue().getSuggestion(),"REJECT");
        assertEquals(productCaptor.getValue().getStatus(),ProductStatus.OFF_SHELF.getCode());
        UserContext.clear();
    }
}
