package com.linlee.cloudsnow.module.staff.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linlee.cloudsnow.module.staff.entity.Staff;

/**
 * 租户管理员Service（店铺老板）
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
public interface StaffService extends IService<Staff> {

    /**
     * 根据用户名查询租户管理员（老板账号）
     */
    Staff getByUsername(String username);

    /**
     * 验证密码
     */
    boolean verifyPassword(String rawPassword, String encodedPassword);
    
    /**
     * 加密密码
     */
    String encodePassword(String rawPassword);
}
