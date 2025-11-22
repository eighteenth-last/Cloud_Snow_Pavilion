package com.linlee.cloudsnow.module.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linlee.cloudsnow.module.coupon.entity.Coupon;

/**
 * 优惠券Service
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
public interface CouponService extends IService<Coupon> {

    /**
     * 领取优惠券
     *
     * @param userId 用户ID
     * @param couponId 优惠券ID
     */
    void receiveCoupon(Long userId, Long couponId);

    /**
     * 使用优惠券
     *
     * @param userId 用户ID
     * @param userCouponId 用户优惠券ID
     */
    void useCoupon(Long userId, Long userCouponId);

    /**
     * 领取优惠券（新）
     *
     * @param userId 用户ID
     * @param couponId 优惠券ID
     */
    void claimCoupon(Long userId, Long couponId);
}
