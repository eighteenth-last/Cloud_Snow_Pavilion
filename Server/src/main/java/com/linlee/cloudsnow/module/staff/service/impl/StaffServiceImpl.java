package com.linlee.cloudsnow.module.staff.service.impl;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linlee.cloudsnow.module.staff.entity.Staff;
import com.linlee.cloudsnow.module.staff.mapper.StaffMapper;
import com.linlee.cloudsnow.module.staff.service.StaffService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * 租户管理员Service实现（店铺老板）
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements StaffService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 根据用户名查询租户管理员（忽略租户过滤）
     * 登录时需要忽略租户过滤，因为还不知道是哪个租户的老板
     */
    @Override
    @InterceptorIgnore(tenantLine = "1")
    public Staff getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<Staff>()
                .eq(Staff::getUsername, username)
                .eq(Staff::getStatus, 1));
    }
    
    /**
     * 根据ID查询（忽略租户过滤）
     * TenantFilter中需要先查询staff才能获取tenant_id
     */
    @Override
    @InterceptorIgnore(tenantLine = "1")
    public Staff getById(Serializable id) {
        return super.getById(id);
    }

    /**
     * 验证密码
     */
    @Override
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 加密密码
     */
    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
