package com.example.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campustrade.common.BusinessException;
import com.example.campustrade.common.PageParamChecker;
import com.example.campustrade.common.RedisKeys;
import com.example.campustrade.convert.PageConvert;
import com.example.campustrade.convert.ProductConvert;
import com.example.campustrade.dto.ProductDTO;
import com.example.campustrade.entity.ProductDO;
import com.example.campustrade.enums.ProductStatus;
import com.example.campustrade.mapper.ProductMapper;
import com.example.campustrade.service.ProductService;
import com.example.campustrade.common.AuthContext;
import com.example.campustrade.vo.PageVO;
import com.example.campustrade.vo.ProductVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    public ProductServiceImpl(ProductMapper productMapper, StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.productMapper = productMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void productPublish(ProductDTO productDTO) {

        Long userId = AuthContext.getCurrentUserId();

        ProductDO productDO = ProductConvert.convertToDO(productDTO,userId);

        productDO.setStatus(ProductStatus.PENDING_REVIEW.getCode());//发布商品时设置商品状态为待审核

        productMapper.insert(productDO);
    }

    @Override
    public PageVO<ProductVO> getAllProducts(Long page,Long pageSize) {
        PageParamChecker.check(page,pageSize);

        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductDO::getStatus,ProductStatus.ON_SALE.getCode());
        wrapper.orderByDesc(ProductDO::getCreateTime);

        Page<ProductDO> pageParam = new Page<>(page,pageSize);
        Page<ProductDO> productDOList = productMapper.selectPage(pageParam,wrapper);//查询商品只查当前上架的商品

        List<ProductVO> productVOList = new ArrayList<>();
        productDOList.getRecords().forEach(productDO -> {
            productVOList.add(ProductConvert.convertToVO(productDO));
        });
        return PageConvert.convert(pageParam,productVOList);
    }

    @Override
    public ProductVO getProductById(Long id) {

        String productKey = RedisKeys.PRODUCT_DETAIL_KEY_PREFIX + id;
        String productJson = stringRedisTemplate.opsForValue().get(productKey);
        if(productJson != null){
            try {
                ProductVO productVO = objectMapper.readValue(productJson,ProductVO.class);
                stringRedisTemplate.opsForZSet().incrementScore(RedisKeys.PRODUCT_HOT_KEY, String.valueOf(id),1);
                return productVO;
            } catch (Exception e) {
                stringRedisTemplate.delete(productKey);
            }
        }

        ProductDO productDO = productMapper.selectById(id);

        //先判断是否存在该商品
        if (productDO == null) {
            throw new BusinessException("不存在该商品");
        }
        if (!ProductStatus.ON_SALE.getCode().equals(productDO.getStatus())){
            throw new BusinessException("商品未上架");
        }

        ProductVO productVO = ProductConvert.convertToVO(productDO);
        try {
            String json = objectMapper.writeValueAsString(productVO);
            stringRedisTemplate.opsForValue().set(productKey,json, Duration.ofMinutes(30));
        } catch (Exception e) {
        }

        stringRedisTemplate.opsForZSet().incrementScore(RedisKeys.PRODUCT_HOT_KEY, String.valueOf(id),1);
        return productVO;
    }

    @Override
    public ProductVO getMyProductById(Long productId) {
        Long userId = AuthContext.getCurrentUserId();
        ProductDO productDO = productMapper.selectById(productId);

        if(productDO == null){
            throw new BusinessException("不存在该商品");
        }

        if(!userId.equals(productDO.getUserId())){
            throw new BusinessException("该用户不是发布者");
        }
        return ProductConvert.convertToVO(productDO);
    }

    @Override
    public PageVO<ProductVO> getAllMyProducts(Integer status,Long page,Long pageSize) {

        Long userId = AuthContext.getCurrentUserId();
        PageParamChecker.check(page,pageSize);

        List<ProductVO> productVOList = new ArrayList<>();
        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductDO::getUserId,userId);
        if(status != null){
            wrapper.eq(ProductDO::getStatus,status);
        }
        wrapper.orderByDesc(ProductDO::getCreateTime);

        Page<ProductDO> pageParam = new Page<>(page,pageSize);
        Page<ProductDO> productDOList = productMapper.selectPage(pageParam,wrapper);

        productDOList.getRecords().forEach(productDO -> {
            productVOList.add(ProductConvert.convertToVO(productDO));
        });


        return PageConvert.convert(pageParam,productVOList);
    }

    @Override
    public PageVO<ProductVO> searchProducts(String keyWord, Long page, Long pageSize) {

        PageParamChecker.check(page,pageSize);

        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductDO::getStatus,ProductStatus.ON_SALE.getCode());
        if (keyWord != null && !keyWord.trim().isEmpty()){
            String keyword = keyWord.trim();
            wrapper.and(w -> w.like(ProductDO::getTitle,keyword)
                    .or()
                    .like(ProductDO::getDescription,keyword));
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
    public List<ProductVO> getHotProducts() {
        Set<String> productIds = stringRedisTemplate.opsForZSet().reverseRange(RedisKeys.PRODUCT_HOT_KEY,0,9);
        List<ProductVO> productVOList = new ArrayList<>();
        if(productIds == null || productIds.isEmpty()){
            return productVOList;
        }
        productIds.forEach(productId ->{
            ProductDO productDO = productMapper.selectById(Long.valueOf(productId));

            if(productDO != null && ProductStatus.ON_SALE.getCode().equals(productDO.getStatus())){
                productVOList.add(ProductConvert.convertToVO(productDO));
            }
        });

        return productVOList;
    }

    @Override
    public void productOff(Long productId) {
        Long userId = AuthContext.getCurrentUserId();
        ProductDO productDO = productMapper.selectById(productId);
        if(productDO == null){
            throw new BusinessException("该商品不存在");
        }
        if(!productDO.getUserId().equals(userId)){
            throw new BusinessException("无权下架该商品");
        }
        if(!ProductStatus.ON_SALE.getCode().equals(productDO.getStatus())){
            throw new BusinessException("商品未上架");
        }
        productDO.setStatus(ProductStatus.OFF_SHELF.getCode());
        productMapper.updateById(productDO);
        stringRedisTemplate.delete(RedisKeys.PRODUCT_DETAIL_KEY_PREFIX+productId);
    }

    @Override
    public void updateProduct(ProductDTO productDTO, Long productId) {
        Long userId = AuthContext.getCurrentUserId();
        ProductDO productDO = productMapper.selectById(productId);
        if(productDO == null){
            throw new BusinessException("该商品不存在");
        }
        if(!productDO.getUserId().equals(userId)){
            throw new BusinessException("无权修改该商品");
        }
        if(!ProductStatus.ON_SALE.getCode().equals(productDO.getStatus())
        && !ProductStatus.PENDING_REVIEW.getCode().equals(productDO.getStatus())){
            throw new BusinessException("商品不可修改");
        }
        productDO.setStatus(ProductStatus.PENDING_REVIEW.getCode());
        productDO.setTitle(productDTO.getTitle());
        productDO.setDescription(productDTO.getDescription());
        productDO.setPrice(productDTO.getPrice());
        productDO.setImageUrl(productDTO.getImageUrl());
        productMapper.updateById(productDO);
        stringRedisTemplate.delete(RedisKeys.PRODUCT_DETAIL_KEY_PREFIX+productId);
    }

}
