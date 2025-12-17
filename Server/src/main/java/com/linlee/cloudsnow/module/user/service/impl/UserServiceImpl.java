package com.linlee.cloudsnow.module.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linlee.cloudsnow.module.user.entity.User;
import com.linlee.cloudsnow.module.user.mapper.UserMapper;
import com.linlee.cloudsnow.module.user.service.UserService;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户Service实现
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 根据手机号查询用户（忽略租户过滤）
     * 登录时需要忽略租户过滤，否则会查不到用户
     */
    @Override
    public User getByMobile(String mobile) {
        try {
            // 设置忽略租户过滤
            com.linlee.cloudsnow.common.context.TenantContext.setIgnore(true);
            return this.getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getMobile, mobile)
                    .eq(User::getStatus, 1)
                    .last("LIMIT 1")); // 如果有多个租户的相同手机号，只返回第一个
        } finally {
            // 清除忽略标记
            com.linlee.cloudsnow.common.context.TenantContext.setIgnore(false);
        }
    }
    
    /**
     * 根据手机号和租户ID查询用户
     */
    @Override
    public User getByMobileAndTenant(String mobile, Long tenantId) {
        try {
            // 设置忽略租户过滤
            com.linlee.cloudsnow.common.context.TenantContext.setIgnore(true);
            return this.getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getMobile, mobile)
                    .eq(User::getTenantId, tenantId)
                    .eq(User::getStatus, 1));
        } finally {
            // 清除忽略标记
            com.linlee.cloudsnow.common.context.TenantContext.setIgnore(false);
        }
    }
    
    /**
     * 根据openid和租户ID查询用户
     */
    public User getByOpenidAndTenant(String openid, Long tenantId) {
        try {
            // 设置忽略租户过滤
            com.linlee.cloudsnow.common.context.TenantContext.setIgnore(true);
            return this.getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getOpenid, openid)
                    .eq(User::getTenantId, tenantId)
                    .eq(User::getStatus, 1));
        } finally {
            // 清除忽略标记
            com.linlee.cloudsnow.common.context.TenantContext.setIgnore(false);
        }
    }
    
    /**
     * 根据ID查询（忽略租户过滤）
     * TenantFilter中需要先查询user才能获取tenant_id
     */
    @Override
    public User getById(Serializable id) {
        try {
            // 设置忽略租户过滤
            com.linlee.cloudsnow.common.context.TenantContext.setIgnore(true);
            return super.getById(id);
        } finally {
            // 清除忽略标记
            com.linlee.cloudsnow.common.context.TenantContext.setIgnore(false);
        }
    }

    @Override
    public User createUser(String mobile, Long tenantId) {
        try {
            // 设置忽略租户过滤
            com.linlee.cloudsnow.common.context.TenantContext.setIgnore(true);
            
            // 检查是否已存在该手机号的用户
            User existUser = this.getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getMobile, mobile));
            
            if (existUser != null) {
                // 用户已存在，直接返回
                return existUser;
            }
            
            User user = new User();
            // C端用户不设置租户ID
            user.setTenantId(null);
            user.setMobile(mobile);
            // 生成昵称：雪阁成员 + 随机字符串
            user.setNick("雪阁成员" + RandomUtil.randomString(12));
            user.setVipLevel(0);
            user.setPoints(0);
            user.setBalance(BigDecimal.ZERO);
            user.setStatus(1);
            
            this.save(user);
            return user;
        } finally {
            // 清除忽略标记
            com.linlee.cloudsnow.common.context.TenantContext.setIgnore(false);
        }
    }
}
