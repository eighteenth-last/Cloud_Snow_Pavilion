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

    /** ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 租户ID */
    private Long tenantId;

    /** 用户ID */
    private Long userId;

    /** 优惠券ID */
    private Long couponId;

    /** 状态：0未使用 1已使用 2已过期 */
    private Integer status;

    /** 领取时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime receiveTime;

    /** 使用时间 */
    private LocalDateTime useTime;
}
