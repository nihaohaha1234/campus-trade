package com.example.campustrade.service;

import com.example.campustrade.vo.OrderVO;
import com.example.campustrade.vo.PageVO;



public interface OrderService {

    void createOrder(Long productId);//创建订单

    PageVO<OrderVO> getBuyerOrders(Integer status,Long page, Long pageSize);//查询所有本人为买家的订单

    PageVO<OrderVO> getSellerOrders(Integer status,Long page,Long pageSize);//查询所有本人为卖家的订单

    OrderVO getOrderById(Long orderId);//根据订单id查 只能查卖家或买家本人的订单

    void cancelOrder(Long orderId);//取消订单

    void confirmOrder(Long orderId);//卖家同意订单

    void finishOrder(Long orderId);//买家或卖家确认完成订单
}
