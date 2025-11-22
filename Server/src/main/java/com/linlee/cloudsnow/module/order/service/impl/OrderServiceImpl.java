package com.linlee.cloudsnow.module.order.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linlee.cloudsnow.common.context.TenantContext;
import com.linlee.cloudsnow.common.exception.BusinessException;
import com.linlee.cloudsnow.module.order.dto.CreateOrderRequest;
import com.linlee.cloudsnow.module.order.entity.OrderItem;
import com.linlee.cloudsnow.module.order.entity.OrderMain;
import com.linlee.cloudsnow.module.order.mapper.OrderItemMapper;
import com.linlee.cloudsnow.module.order.mapper.OrderMainMapper;
import com.linlee.cloudsnow.module.order.service.OrderService;
import com.linlee.cloudsnow.module.product.entity.Sku;
import com.linlee.cloudsnow.module.product.mapper.SkuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单Service实现
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMainMapper, OrderMain> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, CreateOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("订单商品不能为空");
        }

        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            tenantId = 1L; // 默认租户
        }

        // 计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            Sku sku = skuMapper.selectById(item.getSkuId());
            if (sku == null) {
                throw new BusinessException("商品SKU不存在");
            }
            if (sku.getStock() < item.getQuantity()) {
                throw new BusinessException("商品库存不足");
            }
            BigDecimal itemAmount = sku.getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(itemAmount);
        }

        // 创建订单主表
        OrderMain orderMain = new OrderMain();
        orderMain.setTenantId(tenantId);
        orderMain.setUserId(userId);
        orderMain.setOrderType(request.getOrderType());
        orderMain.setStatus(0); // 0-待支付
        orderMain.setAmount(totalAmount);
        this.save(orderMain);

        Long orderId = orderMain.getOrderId();

        // 创建订单明细
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            Sku sku = skuMapper.selectById(item.getSkuId());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setTenantId(tenantId);
            orderItem.setOrderId(orderId);
            orderItem.setSkuId(item.getSkuId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(sku.getPrice());
            orderItemMapper.insert(orderItem);

            // 扣减库存
            sku.setStock(sku.getStock() - item.getQuantity());
            skuMapper.updateById(sku);
        }

        return orderId;
    }

    @Override
    public void cancelOrder(Long orderId, Long userId) {
        OrderMain order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只能取消待支付订单");
        }

        // 更新订单状态为已取消
        order.setStatus(5); // 5-已取消
        this.updateById(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        OrderMain order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setStatus(status);
        
        // 如果是支付成功，记录支付时间
        if (status == 1) {
            order.setPayTime(LocalDateTime.now());
        }
        
        this.updateById(order);
    }
}
