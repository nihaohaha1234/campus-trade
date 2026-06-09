package com.example.campustrade.controller;

import com.example.campustrade.common.Result;
import com.example.campustrade.service.OrderService;
import com.example.campustrade.vo.OrderVO;
import com.example.campustrade.vo.PageVO;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{productId}")
    public Result<Void> createOrder(@PathVariable Long productId){
        orderService.createOrder(productId);
        return Result.success();
    }

    @GetMapping("/buyer")
    public Result<PageVO<OrderVO>> getBuyerOrders(@RequestParam(required = false) Integer status,
                                                  @RequestParam(defaultValue = "1") Long page,
                                                  @RequestParam(defaultValue = "10") Long pageSize){
        return Result.success(orderService.getBuyerOrders(status,page,pageSize));
    }

    @GetMapping("/seller")
    public Result<PageVO<OrderVO>> getSellerOrders(@RequestParam(required = false) Integer status,
                                                   @RequestParam(defaultValue = "1") Long page,
                                                   @RequestParam(defaultValue = "10") Long pageSize){
        return Result.success(orderService.getSellerOrders(status,page,pageSize));
    }

    @GetMapping("/{orderId}")
    public Result<OrderVO> getOrderById(@PathVariable Long orderId){
        return Result.success(orderService.getOrderById(orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return Result.success();
    }

    @PutMapping("/{orderId}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long orderId){
        orderService.confirmOrder(orderId);
        return Result.success();
    }

    @PutMapping("/{orderId}/finish")
    public Result<Void> finishOrder(@PathVariable Long orderId){
        orderService.finishOrder(orderId);
        return Result.success();
    }
}
