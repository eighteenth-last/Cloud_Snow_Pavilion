package com.linlee.cloudsnow.module.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
@TableName("order_main")
public class OrderMain implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单ID */
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    /** 租户ID */
    private Long tenantId;

    /** 用户ID */
    private Long userId;

    /** 订单类型：take自取 / delivery外卖 */
    private String orderType;

    /** 订单状态 */
    private Integer status;

    /** 实付金额 */
    private BigDecimal amount;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
