package com.linlee.cloudsnow.module.auth.dto;

import lombok.Data;

/**
 * 商家注册请求
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Data
public class RegisterRequest {

    /** 店铺名称 */
    private String tenantName;

    /** 管理员手机号 */
    private String mobile;

    /** 登录账号 */
    private String username;

    /** 登录密码 */
    private String password;

    /** 管理员姓名 */
    private String name;
}
