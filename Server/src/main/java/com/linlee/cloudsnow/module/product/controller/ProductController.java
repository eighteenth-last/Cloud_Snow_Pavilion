package com.linlee.cloudsnow.module.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.product.entity.Product;
import com.linlee.cloudsnow.module.product.entity.Sku;
import com.linlee.cloudsnow.module.product.mapper.SkuMapper;
import com.linlee.cloudsnow.module.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private SkuMapper skuMapper;

    @Operation(summary = "获取商品列表")
    @GetMapping("/list")
    public Result<List<Product>> list(@RequestParam(required = false) Long categoryId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreatedAt);
        
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        
        List<Product> list = productService.list(wrapper);
        return Result.success(list);
    }

    @Operation(summary = "分页查询商品")
    @GetMapping("/page")
    public Result<Page<Product>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId) {
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Product::getName, name);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        
        // 按ID倒序排列，最新的在前面
        wrapper.orderByDesc(Product::getId);
        
        Page<Product> page = productService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }

    @Operation(summary = "根据ID获取商品")
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        return Result.success(product);
    }

    @Operation(summary = "搜索商品")
    @GetMapping("/search")
    public Result<List<Product>> search(@RequestParam String keyword) {
        List<Product> list = productService.list(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)
                        .and(wrapper -> wrapper
                                .like(Product::getName, keyword)
                                .or()
                                .like(Product::getSubTitle, keyword))
                        .orderByDesc(Product::getCreatedAt)
        );
        return Result.success(list);
    }

    @Operation(summary = "新增商品")
    @PostMapping
    public Result<Void> add(@RequestBody Product product) {
        productService.save(product);
        return Result.success();
    }

    @Operation(summary = "更新商品")
    @PutMapping
    public Result<Void> update(@RequestBody Product product) {
        productService.updateById(product);
        return Result.success();
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "更新商品状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        productService.updateById(product);
        return Result.success();
    }

    @Operation(summary = "推荐商品")
    @GetMapping("/recommend")
    public Result<List<Product>> recommend(
            @RequestParam(required = false, defaultValue = "6") Integer limit) {
        // 返回热门商品或随机推荐商品
        List<Product> list = productService.list(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)
                        .orderByDesc(Product::getCreatedAt)
                        .last("LIMIT " + limit)
        );
        return Result.success(list);
    }
    
    @Operation(summary = "获取商品SKU列表")
    @GetMapping("/{id}/skus")
    public Result<List<Sku>> getSkusByProductId(@PathVariable Long id) {
        List<Sku> skus = skuMapper.selectList(
                new LambdaQueryWrapper<Sku>()
                        .eq(Sku::getProductId, id)
                        .orderByAsc(Sku::getSkuId)
        );
        return Result.success(skus);
    }
}
