package com.linlee.cloudsnow.module.auth.dto;

import lombok.Data;

/**
 * 登录请求
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
public class LoginRequest {

    /** 手机号（C端小程序登录用） */
    private String mobile;

    /** 验证码（C端小程序登录用） */
    private String code;
    
    /** 租户ID */
    private Long tenantId;
    
    /** 账号（B端后台登录用） */
    private String username;
    
    /** 密码（B端后台登录用） */
    private String password;
}
