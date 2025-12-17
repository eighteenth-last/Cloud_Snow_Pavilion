package com.linlee.cloudsnow.module.order.dto;

import com.linlee.cloudsnow.module.order.entity.OrderItem;
import com.linlee.cloudsnow.module.order.entity.OrderMain;
import lombok.Data;

import java.util.List;

/**
 * 订单详情响应
 *
 * @author Cloud Snow Pavilion
 * @since 2025-12-17
 */
@Data
public class OrderDetailResponse {
    
    /** 订单主信息 */
    private OrderMain order;
    
    /** 订单明细列表 */
    private List<OrderItem> items;
}
