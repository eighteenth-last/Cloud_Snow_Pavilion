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
    @InterceptorIgnore(tenantLine = "1")  // 使用 "1" 而不是 "true"
    public User getByMobile(String mobile) {
        return this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getMobile, mobile)
                .eq(User::getStatus, 1)
                .last("LIMIT 1")); // 如果有多个租户的相同手机号，只返回第一个
    }
    
    /**
     * 根据ID查询（忽略租户过滤）
     * TenantFilter中需要先查询user才能获取tenant_id
     */
    @Override
    @InterceptorIgnore(tenantLine = "1")
    public User getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public User createUser(String mobile, Long tenantId) {
        User user = new User();
        user.setTenantId(tenantId);
        user.setMobile(mobile);
        // 生成昵称：雪阁成员 + 随机12位数字字母
        user.setNick("雪阁成员" + RandomUtil.randomString(12));
        user.setVipLevel(0);
        user.setPoints(0);
        user.setBalance(BigDecimal.ZERO);
        user.setStatus(1);
        
        this.save(user);
        return user;
    }
}
