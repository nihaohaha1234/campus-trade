package com.example.campustrade.service;

import com.example.campustrade.dto.ProductDTO;
import com.example.campustrade.vo.PageVO;
import com.example.campustrade.vo.ProductVO;

import java.util.List;


public interface ProductService {

    void productPublish(ProductDTO productDTO);//发表商品

    PageVO<ProductVO> getAllProducts(Long page,Long pageSize);//查询所有商品 无需登录即可

    PageVO<ProductVO> getRecommendProducts(Long page,Long pageSize);//根据用户最近浏览记录推荐商品

    ProductVO getProductById(Long id);//根据id查询商品 也无需登录 只能看上架中的商品 公开查询存入redis 查询后给商品的热度+1

    ProductVO getMyProductById(Long productId);//根据id查询商品 需要登录 可以查看任何状态的商品

    PageVO<ProductVO> getAllMyProducts(Integer status,Long page,Long pageSize);//查询所有个人发布的商品 需要userid 可以按照状态查询

    PageVO<ProductVO> searchProducts(String keyWord,Long page,Long pageSize);//根据title和description模糊搜索

    List<ProductVO> getHotProducts();//查询热度榜

    void productOff(Long productId);//根据商品id下架商品

    void updateProduct(ProductDTO productDTO,Long productId);//更新商品
}
