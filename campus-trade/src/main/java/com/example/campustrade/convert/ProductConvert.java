package com.example.campustrade.convert;


import com.example.campustrade.common.BusinessException;
import com.example.campustrade.dto.ProductDTO;
import com.example.campustrade.entity.ProductDO;
import com.example.campustrade.vo.ProductVO;

//product的DTO DO VO转换器
public class ProductConvert {

    //DTO转VO
    public static ProductDO convertToDO(ProductDTO productDTO,Long userId){

        if (userId == null) {
            throw new BusinessException("请重新登录");
        }

        ProductDO productDO = new ProductDO();
        productDO.setTitle(productDTO.getTitle());
        productDO.setDescription(productDTO.getDescription());
        productDO.setPrice(productDTO.getPrice());
        productDO.setImageUrl(productDTO.getImageUrl());
        productDO.setUserId(userId);
        return productDO;
    }

    //DO转VO
    public static ProductVO convertToVO(ProductDO productDO){

        ProductVO productVO = new ProductVO();
        productVO.setId(productDO.getId());
        productVO.setUserId(productDO.getUserId());
        productVO.setTitle(productDO.getTitle());
        productVO.setDescription(productDO.getDescription());
        productVO.setPrice(productDO.getPrice());
        productVO.setStatus(productDO.getStatus());
        productVO.setImageUrl(productDO.getImageUrl());

        return productVO;
    }

}
