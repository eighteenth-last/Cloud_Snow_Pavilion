package com.linlee.cloudsnow.module.coupon.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.coupon.entity.Coupon;
import com.linlee.cloudsnow.module.coupon.entity.UserCoupon;
import com.linlee.cloudsnow.module.coupon.mapper.UserCouponMapper;
import com.linlee.cloudsnow.module.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "优惠券管理")
@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserCouponMapper userCouponMapper;

    @Operation(summary = "获取优惠券列表")
    @GetMapping("/list")
    public Result<List<Coupon>> list() {
        List<Coupon> list = couponService.list();
        return Result.success(list);
    }

    @Operation(summary = "分页查询优惠券")
    @GetMapping("/page")
    public Result<Page<Coupon>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Coupon> page = couponService.page(new Page<>(current, size));
        return Result.success(page);
    }

    @Operation(summary = "新增优惠券")
    @PostMapping
    public Result<Void> add(@RequestBody Coupon coupon) {
        couponService.save(coupon);
        return Result.success();
    }

    @Operation(summary = "更新优惠券")
    @PutMapping
    public Result<Void> update(@RequestBody Coupon coupon) {
        couponService.updateById(coupon);
        return Result.success();
    }

    @Operation(summary = "删除优惠券")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        couponService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "领取优惠券")
    @PostMapping("/{id}/receive")
    public Result<Void> receive(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        couponService.receiveCoupon(userId, id);
        return Result.success();
    }

    @Operation(summary = "我的优惠券")
    @GetMapping("/my")
    public Result<List<UserCoupon>> myCoupons(@RequestParam(required = false) Integer status) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId);
        
        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        
        wrapper.orderByDesc(UserCoupon::getReceiveTime);
        
        List<UserCoupon> list = userCouponMapper.selectList(wrapper);
        return Result.success(list);
    }

    @Operation(summary = "获取可领取的优惠券列表")
    @GetMapping("/available")
    public Result<List<Coupon>> getAvailableCoupons() {
        List<Coupon> list = couponService.list(
                new LambdaQueryWrapper<Coupon>()
                        .gt(Coupon::getStock, 0) // 有库存
        );
        return Result.success(list);
    }

    @Operation(summary = "领取优惠券")
    @PostMapping("/{couponId}/claim")
    public Result<Void> claimCoupon(@PathVariable Long couponId) {
        Long userId = StpUtil.getLoginIdAsLong();
        couponService.claimCoupon(userId, couponId);
        return Result.success();
    }
}
