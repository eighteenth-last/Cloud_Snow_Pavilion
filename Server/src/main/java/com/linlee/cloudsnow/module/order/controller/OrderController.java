package com.linlee.cloudsnow.module.order.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.order.dto.CreateOrderRequest;
import com.linlee.cloudsnow.module.order.entity.OrderMain;
import com.linlee.cloudsnow.module.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<Long> createOrder(@RequestBody CreateOrderRequest request) {
        Long userId = StpUtil.getLoginIdAsLong();
        Long orderId = orderService.createOrder(userId, request);
        return Result.success(orderId);
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/my")
    public Result<Page<OrderMain>> myOrders(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        
        Long userId = StpUtil.getLoginIdAsLong();
        
        LambdaQueryWrapper<OrderMain> wrapper = new LambdaQueryWrapper<OrderMain>()
                .eq(OrderMain::getUserId, userId)
                .orderByDesc(OrderMain::getCreateTime);
        
        if (status != null) {
            wrapper.eq(OrderMain::getStatus, status);
        }
        
        Page<OrderMain> page = orderService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderMain> getOrderDetail(@PathVariable Long orderId) {
        OrderMain order = orderService.getById(orderId);
        return Result.success(order);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        Long userId = StpUtil.getLoginIdAsLong();
        orderService.cancelOrder(orderId, userId);
        return Result.success();
    }

    @Operation(summary = "更新订单状态（商家端）")
    @PutMapping("/{orderId}/status")
    public Result<Void> updateStatus(
            @PathVariable Long orderId,
            @RequestParam Integer status) {
        orderService.updateOrderStatus(orderId, status);
        return Result.success();
    }

    @Operation(summary = "所有订单（商家端）")
    @GetMapping("/all")
    public Result<Page<OrderMain>> allOrders(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        
        LambdaQueryWrapper<OrderMain> wrapper = new LambdaQueryWrapper<OrderMain>()
                .orderByDesc(OrderMain::getCreateTime);
        
        if (status != null) {
            wrapper.eq(OrderMain::getStatus, status);
        }
        
        Page<OrderMain> page = orderService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }
}
