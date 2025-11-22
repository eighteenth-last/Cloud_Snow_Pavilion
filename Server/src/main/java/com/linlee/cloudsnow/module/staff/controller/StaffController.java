package com.linlee.cloudsnow.module.staff.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.staff.entity.Staff;
import com.linlee.cloudsnow.module.staff.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "员工管理")
@RestController
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @Operation(summary = "获取员工列表")
    @GetMapping("/list")
    public Result<List<Staff>> list() {
        List<Staff> list = staffService.list(
                new LambdaQueryWrapper<Staff>()
                        .eq(Staff::getStatus, 1)
                        .orderByDesc(Staff::getCreatedAt)
        );
        return Result.success(list);
    }

    @Operation(summary = "分页查询员工")
    @GetMapping("/page")
    public Result<Page<Staff>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        
        LambdaQueryWrapper<Staff> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Staff::getName, name);
        }
        
        wrapper.orderByDesc(Staff::getCreatedAt);
        
        Page<Staff> page = staffService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }

    @Operation(summary = "新增员工")
    @PostMapping
    public Result<Void> add(@RequestBody Staff staff) {
        staffService.save(staff);
        return Result.success();
    }

    @Operation(summary = "更新员工")
    @PutMapping
    public Result<Void> update(@RequestBody Staff staff) {
        staffService.updateById(staff);
        return Result.success();
    }

    @Operation(summary = "删除员工")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        staffService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "更新员工状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Staff staff = new Staff();
        staff.setStaffId(id);
        staff.setStatus(status);
        staffService.updateById(staff);
        return Result.success();
    }
}
