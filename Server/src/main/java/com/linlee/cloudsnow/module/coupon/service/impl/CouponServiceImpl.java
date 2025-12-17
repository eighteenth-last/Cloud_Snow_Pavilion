package com.linlee.cloudsnow.module.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linlee.cloudsnow.common.context.TenantContext;
import com.linlee.cloudsnow.common.exception.BusinessException;
import com.linlee.cloudsnow.module.coupon.entity.Coupon;
import com.linlee.cloudsnow.module.coupon.entity.UserCoupon;
import com.linlee.cloudsnow.module.coupon.mapper.CouponMapper;
import com.linlee.cloudsnow.module.coupon.mapper.UserCouponMapper;
import com.linlee.cloudsnow.module.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 优惠券Service实现
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = this.getById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (coupon.getStock() <= 0) {
            throw new BusinessException("优惠券已领完");
        }
        if (LocalDateTime.now().isBefore(coupon.getStartTime()) ||
            LocalDateTime.now().isAfter(coupon.getEndTime())) {
            throw new BusinessException("优惠券不在有效期内");
        }

        // 减少剩余数量
        coupon.setStock(coupon.getStock() - 1);
        this.updateById(coupon);

        // 创建用户优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setTenantId(TenantContext.getTenantId());
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0); // 未使用
        userCouponMapper.insert(userCoupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useCoupon(Long userId, Long userCouponId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (!userCoupon.getUserId().equals(userId)) {
            throw new BusinessException("无权使用该优惠券");
        }
        if (userCoupon.getStatus() != 1) {
            throw new BusinessException("优惠券已使用或已过期");
        }

        // 更新为已使用
        userCoupon.setStatus(2);
        userCoupon.setUsedTime(LocalDateTime.now());
        userCouponMapper.updateById(userCoupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimCoupon(Long userId, Long couponId) {
        // 查询优惠券
        Coupon coupon = this.getById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }
        
        if (coupon.getStock() <= 0) {
            throw new RuntimeException("优惠券已领完");
        }
        
        // 检查是否已领取
        long count = userCouponMapper.selectCount(
                new LambdaQueryWrapper<UserCoupon>()
                        .eq(UserCoupon::getUserId, userId)
                        .eq(UserCoupon::getCouponId, couponId)
        );
        
        if (count > 0) {
            throw new RuntimeException("已领取过该优惠券");
        }
        
        // 领取优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setTenantId(TenantContext.getTenantId());
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0); // 未使用
        userCouponMapper.insert(userCoupon);
        
        // 减少库存
        coupon.setStock(coupon.getStock() - 1);
        this.updateById(coupon);
    }
}
