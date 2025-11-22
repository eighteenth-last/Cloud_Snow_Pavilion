package com.linlee.cloudsnow.module.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.user.entity.UserAddress;
import com.linlee.cloudsnow.module.user.service.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户地址Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "用户地址管理")
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService addressService;

    @Operation(summary = "获取用户地址列表")
    @GetMapping("/list")
    public Result<List<UserAddress>> getList() {
        Long userId = StpUtil.getLoginIdAsLong();
        List<UserAddress> list = addressService.getUserAddressList(userId);
        return Result.success(list);
    }

    @Operation(summary = "获取地址详情")
    @GetMapping("/{id}")
    public Result<UserAddress> getDetail(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        UserAddress address = addressService.getById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            return Result.fail("地址不存在");
        }
        
        return Result.success(address);
    }

    @Operation(summary = "新增地址")
    @PostMapping
    public Result<Void> add(@RequestBody UserAddress address) {
        Long userId = StpUtil.getLoginIdAsLong();
        address.setUserId(userId);

        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressService.lambdaUpdate()
                    .eq(UserAddress::getUserId, userId)
                    .set(UserAddress::getIsDefault, 0)
                    .update();
        }

        addressService.save(address);
        return Result.success();
    }

    @Operation(summary = "更新地址")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserAddress address) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        UserAddress existAddress = addressService.getById(id);
        if (existAddress == null || !existAddress.getUserId().equals(userId)) {
            return Result.fail("地址不存在");
        }

        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            addressService.lambdaUpdate()
                    .eq(UserAddress::getUserId, userId)
                    .ne(UserAddress::getId, id)
                    .set(UserAddress::getIsDefault, 0)
                    .update();
        }

        address.setId(id);
        address.setUserId(userId);
        addressService.updateById(address);
        
        return Result.success();
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        UserAddress address = addressService.getById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            return Result.fail("地址不存在");
        }

        addressService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "设置默认地址")
    @PostMapping("/{id}/setDefault")
    public Result<Void> setDefault(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        UserAddress address = addressService.getById(id);
        if (address == null || !address.getUserId().equals(userId)) {
            return Result.fail("地址不存在");
        }

        addressService.setDefaultAddress(userId, id);
        return Result.success();
    }

    @Operation(summary = "获取默认地址")
    @GetMapping("/default")
    public Result<UserAddress> getDefaultAddress() {
        Long userId = StpUtil.getLoginIdAsLong();
        UserAddress address = addressService.getDefaultAddress(userId);
        return Result.success(address);
    }
}
