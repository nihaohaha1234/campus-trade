package com.example.campustrade.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.campustrade.common.BusinessException;
import com.example.campustrade.common.UserContext;
import com.example.campustrade.entity.OrderDO;
import com.example.campustrade.entity.ProductDO;
import com.example.campustrade.enums.ProductStatus;
import com.example.campustrade.mapper.OrderMapper;
import com.example.campustrade.mapper.ProductMapper;
import com.example.campustrade.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class OrderServiceTest {
    @Mock
    ProductMapper productMapper;
    @Mock
    OrderMapper orderMapper;
    @Mock
    StringRedisTemplate stringRedisTemplate;

    OrderServiceImpl orderService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        orderService = new OrderServiceImpl(productMapper,orderMapper,stringRedisTemplate);
    }
//创建订单测试
    @Test
    void 正常下单成功(){
        Long userId = 1L;
        Long sellerId = 2L;
        Long productId = 100L;
        ProductDO fakeProduct = new ProductDO();
        fakeProduct.setId(productId);
        fakeProduct.setUserId(sellerId);
        fakeProduct.setPrice(new java.math.BigDecimal("99.00"));
        fakeProduct.setStatus(ProductStatus.ON_SALE.getCode());
        UserContext.setUserId(userId);
        when(productMapper.selectById(productId)).thenReturn(fakeProduct);
        when(productMapper.update(any(),any())).thenReturn(1);
        orderService.createOrder(productId);
        verify(orderMapper,times(1)).insert(any(OrderDO.class));
        UserContext.clear();
    }

    @Test
    void 商品已锁定_抛出异常(){
        Long userId = 1L;
        Long sellerId = 2L;
        Long productId = 100L;
        ProductDO fakeProduct = new ProductDO();
        fakeProduct.setId(productId);
        fakeProduct.setUserId(sellerId);
        fakeProduct.setStatus(ProductStatus.LOCKED.getCode());
        UserContext.setUserId(userId);
        when(productMapper.selectById(productId)).thenReturn(fakeProduct);
        when(productMapper.update(any(),any())).thenReturn(0);
        BusinessException exception = assertThrows(BusinessException.class,()->{
            orderService.createOrder(productId);
        });
        assertTrue(exception.getMessage().contains("已被其他用户下单"));
        verify(orderMapper,never()).insert(any(OrderDO.class));
        UserContext.clear();
    }

    @Test
    void 商品不存在_抛出异常(){
        Long userId = 1L;
        Long productId = 100L;
        UserContext.setUserId(userId);
        when(productMapper.selectById(productId)).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class,()->{
            orderService.createOrder(productId);
        });
        assertTrue(exception.getMessage().contains("商品不存在"));
        UserContext.clear();
    }

    @Test
    void 不能买自己的商品_抛出异常(){
        Long userId = 1L;
        Long productId = 100L;
        UserContext.setUserId(userId);
        ProductDO fakeProduct = new ProductDO();
        fakeProduct.setUserId(userId);
        fakeProduct.setId(productId);
        when(productMapper.selectById(productId)).thenReturn(fakeProduct);
        BusinessException exception = assertThrows(BusinessException.class,()->{
            orderService.createOrder(productId);
        });
        assertTrue(exception.getMessage().contains("不能买自己上架的商品"));
        UserContext.clear();
    }
}
