package com.linlee.cloudsnow.module.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
@TableName("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 优惠券ID */
    @TableId(value = "coupon_id", type = IdType.AUTO)
    private Long couponId;

    /** 租户ID */
    private Long tenantId;

    /** 优惠券标题 */
    private String title;

    /** 使用门槛 */
    private BigDecimal threshold;

    /** 抵扣金额 */
    private BigDecimal amount;

    /** 有效期开始 */
    private LocalDateTime startTime;

    /** 有效期结束 */
    private LocalDateTime endTime;

    /** 发放总量 */
    private Integer totalCount;

    /** 剩余库存 */
    private Integer stock;
}
