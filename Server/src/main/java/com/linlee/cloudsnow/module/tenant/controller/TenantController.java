package com.linlee.cloudsnow.module.tenant.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.tenant.entity.Tenant;
import com.linlee.cloudsnow.module.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "租户管理")
@RestController
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @Operation(summary = "获取租户列表")
    @GetMapping("/list")
    public Result<List<Tenant>> list() {
        List<Tenant> list = tenantService.list(
                new LambdaQueryWrapper<Tenant>()
                        .orderByDesc(Tenant::getCreatedAt)
        );
        return Result.success(list);
    }

    @Operation(summary = "分页查询租户")
    @GetMapping("/page")
    public Result<Page<Tenant>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Tenant> page = tenantService.page(
                new Page<>(current, size),
                new LambdaQueryWrapper<Tenant>()
                        .orderByDesc(Tenant::getCreatedAt)
        );
        return Result.success(page);
    }

    @Operation(summary = "根据ID获取租户")
    @GetMapping("/{id}")
    public Result<Tenant> getById(@PathVariable Long id) {
        Tenant tenant = tenantService.getById(id);
        return Result.success(tenant);
    }

    @Operation(summary = "新增租户")
    @PostMapping
    public Result<Void> add(@RequestBody Tenant tenant) {
        tenantService.save(tenant);
        return Result.success();
    }

    @Operation(summary = "更新租户")
    @PutMapping
    public Result<Void> update(@RequestBody Tenant tenant) {
        tenantService.updateById(tenant);
        return Result.success();
    }

    @Operation(summary = "删除租户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tenantService.removeById(id);
        return Result.success();
    }
}
