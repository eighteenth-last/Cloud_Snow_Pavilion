package com.linlee.cloudsnow.module.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /** 租户ID */
    private Long tenantId;

    /** 微信openid */
    private String openid;

    /** 微信unionid */
    private String unionid;

    /** 昵称 */
    private String nick;

    /** 头像URL */
    private String avatar;

    /** 手机号 */
    private String mobile;

    /** 会员等级 */
    private Integer vipLevel;

    /** 积分 */
    private Integer points;

    /** 账户余额 */
    private BigDecimal balance;

    /** 状态：1正常 0禁用 */
    private Integer status;

    /** 注册时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
