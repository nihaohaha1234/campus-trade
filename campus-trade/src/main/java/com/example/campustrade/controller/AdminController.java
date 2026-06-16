package com.example.campustrade.controller;

import com.example.campustrade.common.Result;
import com.example.campustrade.service.AdminService;
import com.example.campustrade.vo.*;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/admin")
public class AdminController {

    public final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public Result<PageVO<UserVO>> getAllUsersForAdmin(@RequestParam(defaultValue = "1") Long page,
                                                      @RequestParam(defaultValue = "10") Long pageSize){
        return Result.success(adminService.getAllUsersForAdmin(page,pageSize));
    }

    @GetMapping("/orders")
    public Result<PageVO<OrderVO>> getAllOrdersForAdmin(@RequestParam(required = false) Integer status,
                                                        @RequestParam(defaultValue = "1") Long page,
                                                        @RequestParam(defaultValue = "10") Long pageSize){
        return Result.success(adminService.getAllOrdersForAdmin(status, page, pageSize));
    }

    @GetMapping("/products")
    public Result<PageVO<ProductVO>> getAllProducts(@RequestParam(defaultValue = "1") Long page,
                                                    @RequestParam(defaultValue = "20") Long pageSize){
        return Result.success(adminService.getAllProductsForAdmin(page,pageSize));
    }

    @GetMapping("/ai-review-logs")
    public Result<PageVO<AIReviewLogVO>> getAIReviewLogs(@RequestParam(defaultValue = "1") Long page,
                                                         @RequestParam(defaultValue = "10") Long pageSize){
        return Result.success(adminService.getAIReviewLogs(page,pageSize));
    }

    @GetMapping("/products/pending")
    public Result<PageVO<ProductVO>> getAllPendingProducts(@RequestParam(defaultValue = "1") Long page,
                                                           @RequestParam(defaultValue = "20") Long pageSize){
        return Result.success(adminService.getAllPendingProducts(page,pageSize));
    }


    @GetMapping("/products/search")
    public Result<PageVO<ProductVO>> searchProductsForAdmin(@RequestParam(required = false)String keyWord,
                                                            @RequestParam(defaultValue = "1") Long page,
                                                            @RequestParam(defaultValue = "20") Long pageSize){
        return Result.success(adminService.searchProductsForAdmin(keyWord, page, pageSize));
    }

    @GetMapping("/products/{productId}")
    public Result<ProductVO> getProductById(@PathVariable Long productId){
        return Result.success(adminService.getProductByIdForAdmin(productId));
    }

    @PutMapping("/products/{productId}/approve")
    public Result<Void> approveProduct(@PathVariable Long productId){
        adminService.approveProduct(productId);
        return Result.success();
    }

    @PutMapping("/products/{productId}/reject")
    public Result<Void> rejectProduct(@PathVariable Long productId){
        adminService.rejectProduct(productId);
        return Result.success();
    }

    @PutMapping("/users/{userId}/disable")
    public Result<Void> disableUser(@PathVariable Long userId){
        adminService.disableUser(userId);
        return Result.success();
    }

    @PutMapping("/users/{userId}/enable")
    public Result<Void> enableUser(@PathVariable Long userId){
        adminService.enableUser(userId);
        return Result.success();
    }

}
