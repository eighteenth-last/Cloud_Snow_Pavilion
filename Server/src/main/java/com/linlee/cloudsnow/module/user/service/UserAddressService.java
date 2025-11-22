package com.linlee.cloudsnow.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linlee.cloudsnow.module.user.entity.UserAddress;

import java.util.List;

/**
 * 用户地址Service
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 获取用户地址列表
     */
    List<UserAddress> getUserAddressList(Long userId);

    /**
     * 获取用户默认地址
     */
    UserAddress getDefaultAddress(Long userId);

    /**
     * 设置默认地址
     */
    void setDefaultAddress(Long userId, Long addressId);
}
