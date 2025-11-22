package com.linlee.cloudsnow.module.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.linlee.cloudsnow.module.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细Mapper
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
