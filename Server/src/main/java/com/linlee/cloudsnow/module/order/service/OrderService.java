package com.linlee.cloudsnow.module.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linlee.cloudsnow.module.order.dto.CreateOrderRequest;
import com.linlee.cloudsnow.module.order.entity.OrderMain;

/**
 * 订单Service
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
public interface OrderService extends IService<OrderMain> {

    /**
     * 创建订单
     *
     * @param userId 用户ID
     * @param request 订单请求
     * @return 订单ID
     */
    Long createOrder(Long userId, CreateOrderRequest request);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     */
    void cancelOrder(Long orderId, Long userId);

    /**
     * 更新订单状态
     *
     * @param orderId 订单ID
     * @param status 新状态
     */
    void updateOrderStatus(Long orderId, Integer status);
}
