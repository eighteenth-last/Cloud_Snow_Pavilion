package com.linlee.cloudsnow.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linlee.cloudsnow.module.user.entity.User;

/**
 * 用户Service
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
public interface UserService extends IService<User> {

    /**
     * 根据手机号查询用户
     */
    User getByMobile(String mobile);

    /**
     * 创建新用户
     */
    User createUser(String mobile, Long tenantId);
}
