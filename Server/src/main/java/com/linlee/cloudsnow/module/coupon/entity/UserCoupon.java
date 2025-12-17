package com.linlee.cloudsnow.module.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
@TableName("user_coupon")
public class UserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户券ID */
    @TableId(value = "user_coupon_id", type = IdType.AUTO)
    private Long userCouponId;

    /** 租户ID */
    private Long tenantId;

    /** 用户ID */
    private Long userId;

    /** 优惠券ID */
    private Long couponId;

    /** 状态：1未使用 2已使用 3已过期 */
    private Integer status;

    /** 使用时间 */
    @TableField("used_time")
    private LocalDateTime usedTime;

    /** 过期时间 */
    @TableField("expire_time")
    private LocalDateTime expireTime;
}
