package com.example.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campustrade.common.BusinessException;
import com.example.campustrade.common.PageParamChecker;
import com.example.campustrade.common.RedisKeys;
import com.example.campustrade.component.AdminChecker;
import com.example.campustrade.convert.*;
import com.example.campustrade.entity.AIReviewLogDO;
import com.example.campustrade.entity.OrderDO;
import com.example.campustrade.entity.ProductDO;
import com.example.campustrade.entity.UserDO;
import com.example.campustrade.enums.ProductStatus;
import com.example.campustrade.enums.UserRole;
import com.example.campustrade.enums.UserStatus;
import com.example.campustrade.mapper.AIReviewLogMapper;
import com.example.campustrade.mapper.OrderMapper;
import com.example.campustrade.mapper.ProductMapper;
import com.example.campustrade.mapper.UserMapper;
import com.example.campustrade.service.AdminService;
import com.example.campustrade.vo.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {


    private final ProductMapper productMapper;

    private final UserMapper userMapper;

    private final AIReviewLogMapper aiReviewLogMapper;

    private final OrderMapper orderMapper;

    private final AdminChecker adminChecker;

    private final StringRedisTemplate stringRedisTemplate;

    public AdminServiceImpl(ProductMapper productMapper, UserMapper userMapper, AIReviewLogMapper aiReviewLogMapper, OrderMapper orderMapper, AdminChecker adminChecker, StringRedisTemplate stringRedisTemplate) {
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.aiReviewLogMapper = aiReviewLogMapper;
        this.orderMapper = orderMapper;
        this.adminChecker = adminChecker;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    public PageVO<ProductVO> getAllPendingProducts(Long page,Long pageSize) {
        adminChecker.checkAdmin();
        PageParamChecker.check(page,pageSize);

        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductDO::getStatus,ProductStatus.PENDING_REVIEW.getCode());
        wrapper.orderByDesc(ProductDO::getCreateTime);

        Page<ProductDO> pageParam = new Page<>(page,pageSize);
        Page<ProductDO> productDOList = productMapper.selectPage(pageParam,wrapper);

        List<ProductVO> productVOList = new ArrayList<>();
        productDOList.getRecords().forEach(productDO -> {
            productVOList.add(ProductConvert.convertToVO(productDO));
        });
        return PageConvert.convert(pageParam,productVOList);
    }

    @Override
    public void approveProduct(Long productId) {
        adminChecker.checkAdmin();
        ProductDO productDO = productMapper.selectById(productId);
        if (productDO == null){
            throw new BusinessException("商品不存在");
        }
        if (!productDO.getStatus().equals(ProductStatus.PENDING_REVIEW.getCode())){
            throw new BusinessException("商品不处于待审核状态");
        }
        productDO.setStatus(ProductStatus.ON_SALE.getCode());
        productMapper.updateById(productDO);
        stringRedisTemplate.delete(RedisKeys.PRODUCT_DETAIL_KEY_PREFIX+productId);

    }

    @Override
    public void rejectProduct(Long productId) {
        adminChecker.checkAdmin();
        ProductDO productDO = productMapper.selectById(productId);
        if (productDO == null){
            throw new BusinessException("商品不存在");
        }
        if (!productDO.getStatus().equals(ProductStatus.PENDING_REVIEW.getCode())){
            throw new BusinessException("商品不处于待审核状态");
        }
        productDO.setStatus(ProductStatus.OFF_SHELF.getCode());
        productMapper.updateById(productDO);
        stringRedisTemplate.delete(RedisKeys.PRODUCT_DETAIL_KEY_PREFIX+productId);
    }

    @Override
    public PageVO<ProductVO> getAllProductsForAdmin(Long page,Long pageSize) {
        adminChecker.checkAdmin();
        PageParamChecker.check(page,pageSize);

        Page<ProductDO> pageParam = new Page<>(page,pageSize);
        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper();
        wrapper.orderByDesc(ProductDO::getCreateTime);
        Page<ProductDO> productDOList = productMapper.selectPage(pageParam,wrapper);
        List<ProductVO> productVOList = new ArrayList<>();
        productDOList.getRecords().forEach(productDO -> {
            productVOList.add(ProductConvert.convertToVO(productDO));
        });
        return PageConvert.convert(pageParam,productVOList);
    }

    @Override
    public PageVO<ProductVO> searchProductsForAdmin(String keyWord, Long page, Long pageSize) {
        PageParamChecker.check(page,pageSize);
        adminChecker.checkAdmin();

        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
        if (keyWord != null && !keyWord.trim().isEmpty()){
            wrapper.like(ProductDO::getTitle,keyWord)
                    .or()
                    .like(ProductDO::getDescription,keyWord);
        }
        wrapper.orderByDesc(ProductDO::getCreateTime);
        Page<ProductDO> pageParam = new Page<>(page,pageSize);
        Page<ProductDO> productDOList = productMapper.selectPage(pageParam,wrapper);
        List<ProductVO> productVOList = new ArrayList<>();
        productDOList.getRecords().forEach(productDO -> {
            productVOList.add(ProductConvert.convertToVO(productDO));
        });
        return PageConvert.convert(pageParam,productVOList);
    }

    @Override
    public ProductVO getProductByIdForAdmin(Long productId) {
        adminChecker.checkAdmin();
        ProductDO productDO = productMapper.selectById(productId);

        if (productDO == null){
            throw new BusinessException("商品不存在");
        }

        return ProductConvert.convertToVO(productDO);
    }

    @Override
    public PageVO<UserVO> getAllUsersForAdmin(Long page, Long pageSize) {
        adminChecker.checkAdmin();
        PageParamChecker.check(page,pageSize);

        Page<UserDO> pageParam = new Page<>(page,pageSize);
        Page<UserDO> userDOPage = userMapper.selectPage(pageParam,null);
        List<UserVO> userVOList = new ArrayList<>();
        userDOPage.getRecords().forEach(userDO -> {
            userVOList.add(UserConvert.convertToVO(userDO));
        });
        return PageConvert.convert(pageParam,userVOList);
    }

    @Override
    public PageVO<OrderVO> getAllOrdersForAdmin(Integer status, Long page, Long pageSize) {
        adminChecker.checkAdmin();
        PageParamChecker.check(page,pageSize);

        Page<OrderDO> pageParam = new Page<>(page,pageSize);
        LambdaQueryWrapper<OrderDO> wrapper = new LambdaQueryWrapper<>();
        if (status != null){
            wrapper.eq(OrderDO::getStatus,status);
        }
        wrapper.orderByDesc(OrderDO::getCreateTime);
        Page<OrderDO> orderDOPage = orderMapper.selectPage(pageParam,wrapper);
        List<OrderVO> orderVOList = new ArrayList<>();
        orderDOPage.getRecords().forEach(orderDO -> {
            ProductDO productDO = productMapper.selectById(orderDO.getProductId());
            orderVOList.add(OrderConvert.convertToVo(orderDO,productDO));
        });
        return PageConvert.convert(pageParam,orderVOList);
    }

    @Override
    public PageVO<AIReviewLogVO> getAIReviewLogs(Long page, Long pageSize) {
        adminChecker.checkAdmin();
        PageParamChecker.check(page,pageSize);

        Page<AIReviewLogDO> pageParam = new Page<>(page,pageSize);
        LambdaQueryWrapper<AIReviewLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AIReviewLogDO::getCreateTime);
        Page<AIReviewLogDO> aiReviewLogDOPage = aiReviewLogMapper.selectPage(pageParam,wrapper);
        
        List<AIReviewLogVO> aiReviewLogVOList = new ArrayList<>();
        aiReviewLogDOPage.getRecords().forEach(aiReviewLogDO -> {
            aiReviewLogVOList.add(AIReviewLogConvert.convertToVO(aiReviewLogDO));
        });

        return PageConvert.convert(pageParam,aiReviewLogVOList);
    }

    @Override
    public void disableUser(Long userId) {
        adminChecker.checkAdmin();
        UserDO userDO = userMapper.selectById(userId);
        if (userDO == null){
            throw new BusinessException("该用户不存在");
        }
        if(UserStatus.DISABLED.getCode().equals(userDO.getStatus())){
            throw new BusinessException("该用户已被禁用");
        }
        if (userDO.getRole().equals(UserRole.ADMIN.getCode())){
            throw new BusinessException("禁止禁用管理员账号");
        }
        userDO.setStatus(UserStatus.DISABLED.getCode());
        userMapper.updateById(userDO);
    }

    @Override
    public void enableUser(Long userId) {
        adminChecker.checkAdmin();
        UserDO userDO = userMapper.selectById(userId);
        if (userDO == null){
            throw new BusinessException("该用户不存在");
        }
        if(UserStatus.NORMAL.getCode().equals(userDO.getStatus())){
            throw new BusinessException("该用户未在禁用状态");
        }
        userDO.setStatus(UserStatus.NORMAL.getCode());
        userMapper.updateById(userDO);
    }


}
