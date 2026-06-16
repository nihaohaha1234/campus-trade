package com.example.campustrade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campustrade.vo.*;


public interface AdminService {

    PageVO<ProductVO> getAllPendingProducts(Long page,Long pageSize);//获取所有待审核的商品

    void approveProduct(Long productId);//审核通过

    void rejectProduct(Long productId);//审核未通过

    PageVO<ProductVO> getAllProductsForAdmin(Long page,Long pageSize);//管理员可以查询所有的商品

    PageVO<ProductVO> searchProductsForAdmin(String keyWord,Long page,Long pageSize);//管理员模糊查询

    ProductVO getProductByIdForAdmin(Long productId);//管理员根据id查询商品

    PageVO<UserVO> getAllUsersForAdmin(Long page,Long pageSize);//查询用户列表

    PageVO<OrderVO> getAllOrdersForAdmin(Integer status,Long page,Long pageSize);//根据状态查询订单列表

    PageVO<AIReviewLogVO> getAIReviewLogs(Long page, Long pageSize);//管理员查询所有ai审核日志

    void disableUser(Long userId);//封禁用户

    void enableUser(Long userId);//解封用户

}
