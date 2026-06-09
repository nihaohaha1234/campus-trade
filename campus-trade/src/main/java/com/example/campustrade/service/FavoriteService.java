package com.example.campustrade.service;

import com.example.campustrade.vo.PageVO;
import com.example.campustrade.vo.ProductVO;



public interface FavoriteService {
    //收藏商品
    void addFavorite(Long productId);//收藏列表根据商品id添加商品

    //取消收藏
     void removeFavorite(Long productId);//根据id移除商品

    //查看所有收藏
     PageVO<ProductVO> getAllFavorites(Long page,Long pageSize);//根据userid获取所有收藏商品

     Boolean isFavorite(Long productId);//判断该商品是否已收藏
}
