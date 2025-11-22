package com.linlee.cloudsnow.module.offline.dto;

import com.linlee.cloudsnow.module.order.dto.CreateOrderRequest;
import lombok.Data;

import java.util.List;

/**
 * 线下订单请求
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
public class OfflineOrderRequest {

    /** 订单商品项 */
    private List<OrderItemRequest> items;

    /** 备注 */
    private String remark;

    @Data
    public static class OrderItemRequest {
        /** SKU ID */
        private Long skuId;
        
        /** 数量 */
        private Integer quantity;
    }

    /**
     * 转换为CreateOrderRequest
     */
    public CreateOrderRequest toCreateOrderRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setOrderType("take"); // 线下默认自取
        request.setRemark(this.remark);
        
        // 转换商品项
        List<CreateOrderRequest.OrderItemRequest> orderItems = new java.util.ArrayList<>();
        for (OrderItemRequest item : this.items) {
            CreateOrderRequest.OrderItemRequest orderItem = new CreateOrderRequest.OrderItemRequest();
            orderItem.setSkuId(item.getSkuId());
            orderItem.setQuantity(item.getQuantity());
            orderItems.add(orderItem);
        }
        request.setItems(orderItems);
        
        return request;
    }
}
