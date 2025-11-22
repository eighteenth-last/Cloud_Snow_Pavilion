package com.linlee.cloudsnow.module.order.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建订单请求
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
public class CreateOrderRequest {

    /** 订单类型：take自取 / delivery外卖 */
    private String orderType;

    /** 门店ID */
    private Long storeId;

    /** 订单商品项 */
    private List<OrderItemRequest> items;

    /** 优惠券ID（可选） */
    private Long couponId;

    /** 备注 */
    private String remark;

    @Data
    public static class OrderItemRequest {
        /** SKU ID */
        private Long skuId;
        
        /** 数量 */
        private Integer quantity;
    }
}
