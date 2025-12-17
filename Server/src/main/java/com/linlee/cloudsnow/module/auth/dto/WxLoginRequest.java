package com.linlee.cloudsnow.module.auth.dto;

import lombok.Data;

/**
 * 微信登录请求
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
public class WxLoginRequest {

    /** 微信登录code */
    private String code;

    /** 租户ID */
    private Long tenantId;

    /** 微信昵称 */
    private String nickName;

    /** 微信头像URL */
    private String avatarUrl;
}
