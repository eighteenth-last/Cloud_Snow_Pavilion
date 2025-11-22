package com.linlee.cloudsnow.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linlee.cloudsnow.module.user.entity.UserAddress;
import com.linlee.cloudsnow.module.user.mapper.UserAddressMapper;
import com.linlee.cloudsnow.module.user.service.UserAddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户地址Service实现
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    public List<UserAddress> getUserAddressList(Long userId) {
        return this.lambdaQuery()
                .eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreatedAt)
                .list();
    }

    @Override
    public UserAddress getDefaultAddress(Long userId) {
        return this.lambdaQuery()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1)
                .one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long userId, Long addressId) {
        // 取消其他默认地址
        this.lambdaUpdate()
                .eq(UserAddress::getUserId, userId)
                .set(UserAddress::getIsDefault, 0)
                .update();

        // 设置当前地址为默认
        UserAddress address = this.getById(addressId);
        if (address != null && address.getUserId().equals(userId)) {
            address.setIsDefault(1);
            this.updateById(address);
        }
    }
}
