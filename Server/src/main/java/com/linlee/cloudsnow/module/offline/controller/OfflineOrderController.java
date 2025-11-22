package com.linlee.cloudsnow.module.offline.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.offline.dto.OfflineOrderRequest;
import com.linlee.cloudsnow.module.order.entity.OrderMain;
import com.linlee.cloudsnow.module.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 线下点单Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "线下点单")
@RestController
@RequestMapping("/offline")
public class OfflineOrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "快速下单")
    @PostMapping("/quickOrder")
    public Result<Long> quickOrder(@RequestBody OfflineOrderRequest request) {
        // 线下订单使用staff账号创建，userId传0表示线下客户
        Long orderId = orderService.createOrder(0L, request.toCreateOrderRequest());
        // 线下订单直接标记为已支付（现金/扫码支付）
        orderService.updateOrderStatus(orderId, 1);
        return Result.success(orderId);
    }

    @Operation(summary = "今日订单列表")
    @GetMapping("/todayOrders")
    public Result<List<OrderMain>> todayOrders() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        List<OrderMain> list = orderService.list(
                new LambdaQueryWrapper<OrderMain>()
                        .between(OrderMain::getCreateTime, startOfDay, endOfDay)
                        .orderByDesc(OrderMain::getCreateTime)
        );
        return Result.success(list);
    }

    @Operation(summary = "更新订单状态")
    @PutMapping("/{orderId}/status")
    public Result<Void> updateStatus(
            @PathVariable Long orderId,
            @RequestParam Integer status) {
        orderService.updateOrderStatus(orderId, status);
        return Result.success();
    }

    @Operation(summary = "完成订单")
    @PostMapping("/{orderId}/complete")
    public Result<Void> completeOrder(@PathVariable Long orderId) {
        orderService.updateOrderStatus(orderId, 3); // 3-已完成
        return Result.success();
    }
}
