package com.example.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campustrade.common.AuthContext;
import com.example.campustrade.common.BusinessException;
import com.example.campustrade.common.PageParamChecker;
import com.example.campustrade.common.RedisKeys;
import com.example.campustrade.convert.OrderConvert;
import com.example.campustrade.convert.PageConvert;
import com.example.campustrade.entity.OrderDO;
import com.example.campustrade.entity.ProductDO;
import com.example.campustrade.enums.OrderStatus;
import com.example.campustrade.enums.ProductStatus;
import com.example.campustrade.mapper.OrderMapper;
import com.example.campustrade.mapper.ProductMapper;
import com.example.campustrade.service.OrderService;
import com.example.campustrade.utils.OrderNoUtils;
import com.example.campustrade.vo.OrderVO;
import com.example.campustrade.vo.PageVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final ProductMapper productMapper;

    private final OrderMapper orderMapper;

    private final StringRedisTemplate stringRedisTemplate;

    public OrderServiceImpl(ProductMapper productMapper, OrderMapper orderMapper, StringRedisTemplate stringRedisTemplate) {
        this.productMapper = productMapper;
        this.orderMapper = orderMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    @Transactional
    public void createOrder(Long productId) {
        Long userId = AuthContext.getCurrentUserId();
        ProductDO productDO = productMapper.selectById(productId);
        if(productDO == null){
            throw new BusinessException("该商品不存在");
        }
        if(userId.equals(productDO.getUserId())){
            throw new BusinessException("不能买自己上架的商品");
        }

        UpdateWrapper<ProductDO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("status",ProductStatus.ON_SALE.getCode())
                .eq("id",productId)
                .set("status",ProductStatus.LOCKED.getCode());
        int updated = productMapper.update(null,updateWrapper);
        if(updated == 0){
            throw new BusinessException("商品已被其他用户下单或当前不可交易");
        }

        OrderDO orderDO = new OrderDO();
        orderDO.setStatus(OrderStatus.WAIT_CONFIRM.getCode());
        orderDO.setBuyerId(userId);
        orderDO.setProductId(productId);
        orderDO.setOrderNo(OrderNoUtils.generateOrderNo());
        orderDO.setPrice(productDO.getPrice());
        orderDO.setSellerId(productDO.getUserId());

        stringRedisTemplate.delete(RedisKeys.PRODUCT_DETAIL_KEY_PREFIX+productId);

        orderMapper.insert(orderDO);
    }

    @Override
    public PageVO<OrderVO> getBuyerOrders(Integer status,Long page,Long pageSize) {
        Long userId = AuthContext.getCurrentUserId();
        PageParamChecker.check(page,pageSize);

        LambdaQueryWrapper<OrderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDO::getBuyerId,userId);
        wrapper.orderByDesc(OrderDO::getCreateTime);
        if (status != null){
            wrapper.eq(OrderDO::getStatus,status);
        }

        Page<OrderDO> pageParam = new Page<>(page,pageSize);
        Page<OrderDO> orderDOList = orderMapper.selectPage(pageParam,wrapper);
        List<OrderVO> orderVOList = new ArrayList<>();
        orderDOList.getRecords().forEach(orderDO -> {
            ProductDO productDO = productMapper.selectById(orderDO.getProductId());
            orderVOList.add(OrderConvert.convertToVo(orderDO,productDO));
        });
        return PageConvert.convert(pageParam,orderVOList);
    }

    @Override
    public PageVO<OrderVO> getSellerOrders(Integer status,Long page,Long pageSize) {
        Long userId = AuthContext.getCurrentUserId();
        PageParamChecker.check(page,pageSize);

        LambdaQueryWrapper<OrderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderDO::getSellerId,userId);
        wrapper.orderByDesc(OrderDO::getCreateTime);
        if (status != null){
            wrapper.eq(OrderDO::getStatus,status);
        }

        Page<OrderDO> pageParam = new Page<>(page,pageSize);

        Page<OrderDO> orderDOList = orderMapper.selectPage(pageParam,wrapper);
        List<OrderVO> orderVOList = new ArrayList<>();
        orderDOList.getRecords().forEach(orderDO -> {
            ProductDO productDO = productMapper.selectById(orderDO.getProductId());
            orderVOList.add(OrderConvert.convertToVo(orderDO,productDO));
        });
        return PageConvert.convert(pageParam,orderVOList);
    }

    @Override
    public OrderVO getOrderById(Long orderId) {
        Long userId = AuthContext.getCurrentUserId();
        OrderDO orderDO = orderMapper.selectById(orderId);
        if(orderDO == null){
            throw new BusinessException("该订单不存在");
        }
        if(!orderDO.getBuyerId().equals(userId) && !orderDO.getSellerId().equals(userId)){
            throw new BusinessException("没有查看该订单权限");
        }
        ProductDO productDO = productMapper.selectById(orderDO.getProductId());
        if (productDO == null){
            throw new BusinessException("该商品不存在");
        }
        return OrderConvert.convertToVo(orderDO,productDO);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {

        OrderDO orderDO = orderMapper.selectById(orderId);
        Long userId = AuthContext.getCurrentUserId();
        if (orderDO == null){
            throw new BusinessException("订单不存在");
        }
        if(!orderDO.getSellerId().equals(userId)
        &&!orderDO.getBuyerId().equals(userId)){
            throw new BusinessException("只能取消自己的订单");
        }
        if(!OrderStatus.WAIT_CONFIRM.getCode().equals(orderDO.getStatus())
        && !OrderStatus.CONFIRMED.getCode().equals(orderDO.getStatus())){
            throw new BusinessException("当前订单无法取消");
        }
        orderDO.setStatus(OrderStatus.CANCELED.getCode());
        orderMapper.updateById(orderDO);

        ProductDO productDO = productMapper.selectById(orderDO.getProductId());
        if(productDO == null){
            throw new BusinessException("商品不存在");
        }
        productDO.setStatus(ProductStatus.ON_SALE.getCode());
        productMapper.updateById(productDO);
        stringRedisTemplate.delete(RedisKeys.PRODUCT_DETAIL_KEY_PREFIX + productDO.getId());
    }

    @Override
    @Transactional
    public void confirmOrder(Long orderId) {
        Long userId = AuthContext.getCurrentUserId();
        OrderDO orderDO = orderMapper.selectById(orderId);
        if (orderDO == null){
            throw new BusinessException("订单不存在");
        }
        if(!orderDO.getSellerId().equals(userId)){
            throw new BusinessException("只能确认自己发布商品的订单");
        }
        if(!OrderStatus.WAIT_CONFIRM.getCode().equals(orderDO.getStatus())){
            throw new BusinessException("订单状态错误");
        }
        orderDO.setStatus(OrderStatus.CONFIRMED.getCode());
        orderMapper.updateById(orderDO);
    }

    @Override
    @Transactional
    public void finishOrder(Long orderId) {
        Long userId = AuthContext.getCurrentUserId();
        OrderDO orderDO = orderMapper.selectById(orderId);
        if (orderDO == null){
            throw new BusinessException("订单不存在");
        }
        if(!orderDO.getSellerId().equals(userId)
        && !orderDO.getBuyerId().equals(userId)){
            throw new BusinessException("只能完成自己的订单");
        }
        if(!OrderStatus.CONFIRMED.getCode().equals(orderDO.getStatus())){
            throw new BusinessException("订单状态错误");
        }
        orderDO.setStatus(OrderStatus.FINISHED.getCode());
        orderMapper.updateById(orderDO);
        ProductDO productDO = productMapper.selectById(orderDO.getProductId());
        if(productDO == null){
            throw new BusinessException("商品不存在");
        }
        productDO.setStatus(ProductStatus.SOLD.getCode());
        productMapper.updateById(productDO);
        stringRedisTemplate.delete(RedisKeys.PRODUCT_DETAIL_KEY_PREFIX + productDO.getId());
    }
}
