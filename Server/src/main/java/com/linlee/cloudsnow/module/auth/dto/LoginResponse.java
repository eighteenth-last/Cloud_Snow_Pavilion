package com.linlee.cloudsnow.module.auth.dto;

import lombok.Data;

/**
 * 登录响应
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
public class LoginResponse {

    /** Token */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 昵称 */
    private String nick;

    /** 头像 */
    private String avatar;

    /** 租户ID */
    private Long tenantId;
}
