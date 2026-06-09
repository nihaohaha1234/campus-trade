package com.example.campustrade.convert;

import com.example.campustrade.entity.OrderDO;
import com.example.campustrade.entity.ProductDO;
import com.example.campustrade.vo.OrderVO;

public class OrderConvert {

    public static OrderVO convertToVo(OrderDO orderDO, ProductDO productDO){
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(orderDO.getOrderNo());
        orderVO.setPrice(orderDO.getPrice());
        orderVO.setId(orderDO.getId());
        orderVO.setStatus(orderDO.getStatus());
        orderVO.setBuyerId(orderDO.getBuyerId());
        orderVO.setSellerId(orderDO.getSellerId());
        orderVO.setProductId(orderDO.getProductId());
        orderVO.setProductTitle(productDO.getTitle());
        orderVO.setImageUrl(productDO.getImageUrl());
        orderVO.setCreateTime(orderDO.getCreateTime());
        return orderVO;
    }
}
