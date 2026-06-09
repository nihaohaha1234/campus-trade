package com.example.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campustrade.common.BusinessException;
import com.example.campustrade.common.PageParamChecker;
import com.example.campustrade.common.UserContext;
import com.example.campustrade.convert.PageConvert;
import com.example.campustrade.convert.ProductConvert;
import com.example.campustrade.entity.FavoriteDO;
import com.example.campustrade.entity.ProductDO;
import com.example.campustrade.mapper.FavoriteMapper;
import com.example.campustrade.mapper.ProductMapper;
import com.example.campustrade.service.FavoriteService;
import com.example.campustrade.common.AuthContext;
import com.example.campustrade.vo.PageVO;
import com.example.campustrade.vo.ProductVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    private final ProductMapper productMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper, ProductMapper productMapper) {
        this.favoriteMapper = favoriteMapper;
        this.productMapper = productMapper;
    }

    @Override
    public void addFavorite(Long productId) {

        ProductDO productDO = getProductOrThrow(productId);
        Long userId = AuthContext.getCurrentUserId();

        //添加商品前先判断商品是否已在表中 如果存在则报错
        LambdaQueryWrapper<FavoriteDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteDO::getProductId,productId)
                .eq(FavoriteDO::getUserId,userId);

        FavoriteDO existFavorite = favoriteMapper.selectOne(wrapper);
        if (existFavorite != null){
            throw new BusinessException("商品已收藏");
        }

        FavoriteDO favoriteDO = new FavoriteDO();
        favoriteDO.setUserId(userId);
        favoriteDO.setProductId(productDO.getId());
        favoriteMapper.insert(favoriteDO);

    }

    @Override
    public void removeFavorite(Long productId) {

        ProductDO productDO = getProductOrThrow(productId);
        Long userId = AuthContext.getCurrentUserId();

        LambdaQueryWrapper<FavoriteDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteDO::getProductId,productId)
                        .eq(FavoriteDO::getUserId,userId);
        int deleted = favoriteMapper.delete(wrapper);
        if(deleted == 0){
            throw new BusinessException("未收藏该商品");
        }

    }

    @Override
    public PageVO<ProductVO> getAllFavorites(Long page,Long pageSize) {

        Long userId = AuthContext.getCurrentUserId();
        PageParamChecker.check(page,pageSize);

        LambdaQueryWrapper<FavoriteDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteDO::getUserId,userId);
        wrapper.orderByDesc(FavoriteDO::getCreateTime);

        Page<FavoriteDO> pageParam = new Page<>(page,pageSize);
        Page<FavoriteDO> favoriteDOList = favoriteMapper.selectPage(pageParam,wrapper);

        List<ProductVO> productVOList = new ArrayList<>();

        favoriteDOList.getRecords().forEach(favoriteDO -> {
            ProductDO productDO = productMapper.selectById(favoriteDO.getProductId());
            if (productDO != null){
                productVOList.add(ProductConvert.convertToVO(productDO));
            }
        });
        return PageConvert.convert(pageParam,productVOList);
    }

    @Override
    public Boolean isFavorite(Long productId) {
        Long userId = AuthContext.getCurrentUserId();
        LambdaQueryWrapper<FavoriteDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoriteDO::getUserId,userId)
                .eq(FavoriteDO::getProductId,productId);
        return favoriteMapper.selectCount(wrapper)>0;
    }

    //根据productid查询productdo
    private ProductDO getProductOrThrow(Long productId){
        ProductDO productDO = productMapper.selectById(productId);

        if(productDO == null){
            throw new BusinessException("商品不存在");
        }
        return productDO;
    }

}
