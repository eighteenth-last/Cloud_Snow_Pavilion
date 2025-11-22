package com.linlee.cloudsnow.module.ingredient.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.ingredient.entity.Ingredient;
import com.linlee.cloudsnow.module.ingredient.entity.StockLog;
import com.linlee.cloudsnow.module.ingredient.mapper.StockLogMapper;
import com.linlee.cloudsnow.module.ingredient.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 原料Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "原料管理")
@RestController
@RequestMapping("/ingredient")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private StockLogMapper stockLogMapper;

    @Operation(summary = "获取原料列表")
    @GetMapping("/list")
    public Result<List<Ingredient>> list() {
        List<Ingredient> list = ingredientService.list(
                new LambdaQueryWrapper<Ingredient>()
                        .orderByDesc(Ingredient::getCreatedAt)
        );
        return Result.success(list);
    }

    @Operation(summary = "分页查询原料")
    @GetMapping("/page")
    public Result<Page<Ingredient>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Ingredient> page = ingredientService.page(new Page<>(current, size));
        return Result.success(page);
    }

    @Operation(summary = "新增原料")
    @PostMapping
    public Result<Void> add(@RequestBody Ingredient ingredient) {
        ingredientService.save(ingredient);
        return Result.success();
    }

    @Operation(summary = "更新原料")
    @PutMapping
    public Result<Void> update(@RequestBody Ingredient ingredient) {
        ingredientService.updateById(ingredient);
        return Result.success();
    }

    @Operation(summary = "删除原料")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        ingredientService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "入库")
    @PostMapping("/{id}/in")
    public Result<Void> stockIn(
            @PathVariable Long id,
            @RequestParam Integer quantity,
            @RequestParam String operator,
            @RequestParam(required = false) String remark) {
        ingredientService.stockIn(id, quantity, operator, remark);
        return Result.success();
    }

    @Operation(summary = "出库")
    @PostMapping("/{id}/out")
    public Result<Void> stockOut(
            @PathVariable Long id,
            @RequestParam Integer quantity,
            @RequestParam String operator,
            @RequestParam(required = false) String remark) {
        ingredientService.stockOut(id, quantity, operator, remark);
        return Result.success();
    }

    @Operation(summary = "库存日志")
    @GetMapping("/logs")
    public Result<Page<StockLog>> logs(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long ingredientId) {
        
        LambdaQueryWrapper<StockLog> wrapper = new LambdaQueryWrapper<StockLog>()
                .orderByDesc(StockLog::getCreatedAt);
        
        if (ingredientId != null) {
            wrapper.eq(StockLog::getIngredientId, ingredientId);
        }
        
        Page<StockLog> page = new Page<>(current, size);
        stockLogMapper.selectPage(page, wrapper);
        return Result.success(page);
    }
}
