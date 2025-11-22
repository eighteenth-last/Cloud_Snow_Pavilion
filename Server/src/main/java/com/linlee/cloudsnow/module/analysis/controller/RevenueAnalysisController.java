package com.linlee.cloudsnow.module.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.analysis.dto.RevenueStatistics;
import com.linlee.cloudsnow.module.analysis.dto.ProductSalesStatistics;
import com.linlee.cloudsnow.module.order.entity.OrderMain;
import com.linlee.cloudsnow.module.order.entity.OrderItem;
import com.linlee.cloudsnow.module.order.mapper.OrderMainMapper;
import com.linlee.cloudsnow.module.order.mapper.OrderItemMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 收入分析Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "收入分析")
@RestController
@RequestMapping("/analysis")
public class RevenueAnalysisController {

    @Autowired
    private OrderMainMapper orderMainMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Operation(summary = "收入统计")
    @GetMapping("/revenue")
    public Result<RevenueStatistics> revenueStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDateTime start = startDate != null ? 
            LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
            LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        LocalDateTime end = endDate != null ?
            LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
            LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        // 查询已支付订单
        List<OrderMain> orders = orderMainMapper.selectList(
                new LambdaQueryWrapper<OrderMain>()
                        .ge(OrderMain::getStatus, 1) // 已支付及以上状态
                        .between(OrderMain::getPayTime, start, end)
        );
        
        // 统计数据
        RevenueStatistics stats = new RevenueStatistics();
        stats.setOrderCount(orders.size());
        stats.setTotalRevenue(orders.stream()
                .map(OrderMain::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        if (orders.size() > 0) {
            stats.setAvgOrderAmount(stats.getTotalRevenue()
                    .divide(new BigDecimal(orders.size()), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            stats.setAvgOrderAmount(BigDecimal.ZERO);
        }
        
        return Result.success(stats);
    }

    @Operation(summary = "商品销量统计")
    @GetMapping("/productSales")
    public Result<List<ProductSalesStatistics>> productSalesStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDateTime start = startDate != null ? 
            LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
            LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        
        LocalDateTime end = endDate != null ?
            LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
            LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        // 查询已支付订单
        List<OrderMain> orders = orderMainMapper.selectList(
                new LambdaQueryWrapper<OrderMain>()
                        .ge(OrderMain::getStatus, 1)
                        .between(OrderMain::getPayTime, start, end)
        );
        
        if (orders.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        
        List<Long> orderIds = orders.stream()
                .map(OrderMain::getOrderId)
                .collect(Collectors.toList());
        
        // 查询订单明细
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .in(OrderItem::getOrderId, orderIds)
        );
        
        // 按SKU统计
        Map<Long, ProductSalesStatistics> statsMap = new HashMap<>();
        for (OrderItem item : items) {
            Long skuId = item.getSkuId();
            ProductSalesStatistics stats = statsMap.getOrDefault(skuId, new ProductSalesStatistics());
            stats.setSkuId(skuId);
            stats.setSalesCount(stats.getSalesCount() + item.getQuantity());
            stats.setSalesAmount(stats.getSalesAmount().add(
                    item.getPrice().multiply(new BigDecimal(item.getQuantity()))
            ));
            statsMap.put(skuId, stats);
        }
        
        // 按销量排序
        List<ProductSalesStatistics> result = new ArrayList<>(statsMap.values());
        result.sort((a, b) -> b.getSalesCount() - a.getSalesCount());
        
        return Result.success(result);
    }

    @Operation(summary = "今日收入概览")
    @GetMapping("/todayOverview")
    public Result<RevenueStatistics> todayOverview() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        List<OrderMain> orders = orderMainMapper.selectList(
                new LambdaQueryWrapper<OrderMain>()
                        .ge(OrderMain::getStatus, 1)
                        .between(OrderMain::getPayTime, startOfDay, endOfDay)
        );
        
        RevenueStatistics stats = new RevenueStatistics();
        stats.setOrderCount(orders.size());
        stats.setTotalRevenue(orders.stream()
                .map(OrderMain::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        if (orders.size() > 0) {
            stats.setAvgOrderAmount(stats.getTotalRevenue()
                    .divide(new BigDecimal(orders.size()), 2, BigDecimal.ROUND_HALF_UP));
        } else {
            stats.setAvgOrderAmount(BigDecimal.ZERO);
        }
        
        return Result.success(stats);
    }
}
