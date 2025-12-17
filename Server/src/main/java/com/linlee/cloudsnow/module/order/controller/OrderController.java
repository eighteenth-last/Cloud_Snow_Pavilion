package com.linlee.cloudsnow.module.order.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.common.util.LoginUserUtil;
import com.linlee.cloudsnow.module.order.dto.CreateOrderRequest;
import com.linlee.cloudsnow.module.order.dto.OrderDetailResponse;
import com.linlee.cloudsnow.module.order.entity.OrderItem;
import com.linlee.cloudsnow.module.order.entity.OrderMain;
import com.linlee.cloudsnow.module.order.mapper.OrderItemMapper;
import com.linlee.cloudsnow.module.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<Long> createOrder(@RequestBody CreateOrderRequest request) {
        Long userId = LoginUserUtil.getCurrentUserId();
        
        // C端用户直接下单
        if (userId != null) {
            Long orderId = orderService.createOrder(userId, request);
            return Result.success(orderId);
        }
        
        // B端管理员线下下单（代客下单）
        if (LoginUserUtil.isAdmin()) {
            // 线下订单，使用特殊用户ID 0 表示线下客户
            Long orderId = orderService.createOrder(0L, request);
            return Result.success(orderId);
        }
        
        return Result.fail("请先登录");
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/my")
    public Result<Page<OrderMain>> myOrders(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        
        Long userId = LoginUserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请使用小程序用户账号");
        }
        
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
    public Result<OrderDetailResponse> getOrderDetail(@PathVariable Long orderId) {
        // 查询订单主信息
        OrderMain order = orderService.getById(orderId);
        
        // 查询订单明细
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, orderId)
        );
        
        // 组装响应
        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrder(order);
        response.setItems(items);
        
        return Result.success(response);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        Long userId = LoginUserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.fail("请使用小程序用户账号");
        }
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
