package com.example.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campustrade.common.*;
import com.example.campustrade.convert.PageConvert;
import com.example.campustrade.convert.ProductConvert;
import com.example.campustrade.dto.ProductAIDTO;
import com.example.campustrade.dto.ProductDTO;
import com.example.campustrade.entity.ProductDO;
import com.example.campustrade.enums.ProductStatus;
import com.example.campustrade.mapper.ProductMapper;
import com.example.campustrade.service.AIService;
import com.example.campustrade.service.ProductService;
import com.example.campustrade.vo.PageVO;
import com.example.campustrade.vo.ProductAIReviewVO;
import com.example.campustrade.vo.ProductVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    private final AIService aiService;

    public ProductServiceImpl(ProductMapper productMapper, StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper, AIService aiService) {
        this.productMapper = productMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.aiService = aiService;
    }

    @Override
    public void productPublish(ProductDTO productDTO) {

        Long userId = AuthContext.getCurrentUserId();

        ProductAIDTO productAIDTO = new ProductAIDTO();
        productAIDTO.setTitle(productDTO.getTitle());
        productAIDTO.setDescription(productDTO.getDescription());
        productAIDTO.setPrice(productDTO.getPrice());

        ProductAIReviewVO productAIReviewVO = aiService.reviewProduct(productAIDTO);

        if (productAIReviewVO == null || productAIReviewVO.getSuggestion() == null) {
            throw new BusinessException("AI审核失败，请稍后再试");
        }

        if ("REJECT".equals(productAIReviewVO.getSuggestion())){
            throw new BusinessException("AI审核未通过:"+productAIReviewVO.getReason());
        }

        if (!"REJECT".equals(productAIReviewVO.getSuggestion())){
            throw new BusinessException("AI审核异常，请稍后再试");
        }

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
    public PageVO<ProductVO> getRecommendProducts(Long page, Long pageSize) {
        PageParamChecker.check(page, pageSize);

        Long userId = AuthContext.getCurrentUserId();
        String key= RedisKeys.USER_BROWSE_KEY+userId;
        List<String> browseProducts = stringRedisTemplate.opsForList().range(key,0,-1);

        if (browseProducts == null || browseProducts.isEmpty()){
            return getAllProducts(page,pageSize);
        }

        List<Long> browseProductsIds = new ArrayList<>();
        browseProducts.forEach(browseProduct->{
            browseProductsIds.add(Long.valueOf(browseProduct));
        });
        List<String> keyWords = new ArrayList<>();
        browseProductsIds.forEach(browseProductId->{
            ProductDO browseProductDO = productMapper.selectById(browseProductId);
            if (browseProductDO != null){
                keyWords.add(browseProductDO.getTitle());
            }
        });

        if (keyWords.isEmpty()){
            return getAllProducts(page,pageSize);
        }

        //构建wrapper
        LambdaQueryWrapper<ProductDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductDO::getStatus,ProductStatus.ON_SALE.getCode());

        if (!browseProductsIds.isEmpty()){
            wrapper.notIn(ProductDO::getId,browseProductsIds);
        }

        wrapper.and(w->{
            for (int i = 0;i<keyWords.size();i++){
                if (i > 0)w.or();
                String keyWord = keyWords.get(i);
                w.like(ProductDO::getTitle,keyWord)
                        .or()
                        .like(ProductDO::getDescription,keyWord);
            }
        });

        //查推荐商品
       List<ProductDO> recommmendProductDOList = productMapper.selectList(wrapper);
       if (recommmendProductDOList == null || recommmendProductDOList.isEmpty()){
           return getAllProducts(page,pageSize);
       }
       List<ProductVO> recommendProductVOList = new ArrayList<>();
       recommmendProductDOList.forEach(productDO -> {
           recommendProductVOList.add(ProductConvert.convertToVO(productDO));
       });

        //用普通商品补齐商品列表
        List<Long> excludeIds = new ArrayList<>();

        recommendProductVOList.forEach(productVO -> {
            excludeIds.add(productVO.getId());
        });

        LambdaQueryWrapper<ProductDO> normalWrapper = new LambdaQueryWrapper<>();
        normalWrapper.eq(ProductDO::getStatus,ProductStatus.ON_SALE.getCode());
        normalWrapper.orderByDesc(ProductDO::getCreateTime);

        if (!excludeIds.isEmpty()){
            normalWrapper.notIn(ProductDO::getId,excludeIds);
        }

        List<ProductDO> normalProductDOs = productMapper.selectList(normalWrapper);
        List<ProductVO> normalProductVOs = new ArrayList<>();
        normalProductDOs.forEach(normalProductDO->{
            normalProductVOs.add(ProductConvert.convertToVO(normalProductDO));
        });

        List<ProductVO> finalProductVOList = new ArrayList<>();
        finalProductVOList.addAll(recommendProductVOList);
        finalProductVOList.addAll(normalProductVOs);

        //手动分页
        int total = finalProductVOList.size();
        Long pages = (total+pageSize-1)/pageSize;
        int fromIndex = (page.intValue()-1)*pageSize.intValue();
        int toIndex = Math.min(fromIndex + pageSize.intValue(),finalProductVOList.size());

        List<ProductVO> productVOList = new ArrayList<>();
        if (fromIndex < finalProductVOList.size()){
            productVOList = finalProductVOList.subList(fromIndex,toIndex);
        }

        PageVO<ProductVO> pageVO = new PageVO<>();
        pageVO.setTotal(Long.valueOf(total));
        pageVO.setPages(pages);
        pageVO.setCurrent(page);
        pageVO.setSize(pageSize);
        pageVO.setRecords(productVOList);

        return pageVO;
    }

    @Override
    public ProductVO getProductById(Long id) {

        Long userId = UserContext.getUserId();
        if (userId != null){
            String userBrowseKey = RedisKeys.USER_BROWSE_KEY+userId;
            stringRedisTemplate.opsForList().remove(userBrowseKey,0,String.valueOf(id));//先查重删除所有这个id的商品然后再加入
            stringRedisTemplate.opsForList().leftPush(userBrowseKey,String.valueOf(id));
            stringRedisTemplate.opsForList().trim(userBrowseKey,0,9);
            stringRedisTemplate.expire(userBrowseKey,Duration.ofDays(7));
        }

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
