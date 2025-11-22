package com.linlee.cloudsnow.module.store.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.store.entity.Store;
import com.linlee.cloudsnow.module.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 门店Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "门店管理")
@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Operation(summary = "获取门店列表")
    @GetMapping("/list")
    public Result<List<Store>> list() {
        List<Store> list = storeService.list(
                new LambdaQueryWrapper<Store>()
                        .eq(Store::getStatus, 1)
                        .orderByDesc(Store::getId)
        );
        return Result.success(list);
    }

    @Operation(summary = "分页查询门店")
    @GetMapping("/page")
    public Result<Page<Store>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Store> page = storeService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<Store>()
                        .orderByDesc(Store::getId)
        );
        return Result.success(page);
    }

    @Operation(summary = "根据ID获取门店")
    @GetMapping("/{id}")
    public Result<Store> getById(@PathVariable Long id) {
        Store store = storeService.getById(id);
        return Result.success(store);
    }

    @Operation(summary = "获取附近门店")
    @GetMapping("/nearby")
    public Result<List<Store>> getNearbyStores(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "10.0") Double distance) {
        List<Store> stores = storeService.getNearbyStores(lat, lng, distance);
        return Result.success(stores);
    }

    @Operation(summary = "新增门店")
    @PostMapping
    public Result<Void> add(@RequestBody Store store) {
        storeService.save(store);
        return Result.success();
    }

    @Operation(summary = "更新门店")
    @PutMapping
    public Result<Void> update(@RequestBody Store store) {
        storeService.updateById(store);
        return Result.success();
    }

    @Operation(summary = "删除门店")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        storeService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "更新门店状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Store store = new Store();
        store.setId(id);
        store.setStatus(status);
        storeService.updateById(store);
        return Result.success();
    }
}
