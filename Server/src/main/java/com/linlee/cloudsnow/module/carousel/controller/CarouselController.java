package com.linlee.cloudsnow.module.carousel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.carousel.entity.Carousel;
import com.linlee.cloudsnow.module.carousel.service.CarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * 轮播图Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-12-17
 */
@Tag(name = "轮播图管理")
@RestController
@RequestMapping("/carousel")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    // 轮播图上传路径
    private static final String UPLOAD_PATH = "R:\\Code_Repository\\Cloud_Snow_Pavilion\\upload_img\\Carousel\\";

    @Operation(summary = "获取轮播图列表（前台）")
    @GetMapping("/list")
    public Result<List<Carousel>> list() {
        List<Carousel> list = carouselService.list(
                new LambdaQueryWrapper<Carousel>()
                        .eq(Carousel::getStatus, 1)
                        .orderByAsc(Carousel::getSort)
        );
        return Result.success(list);
    }

    @Operation(summary = "分页查询轮播图（后台）")
    @GetMapping("/page")
    public Result<Page<Carousel>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<Carousel> wrapper = new LambdaQueryWrapper<>();

        if (title != null && !title.trim().isEmpty()) {
            wrapper.like(Carousel::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(Carousel::getStatus, status);
        }

        wrapper.orderByAsc(Carousel::getSort).orderByDesc(Carousel::getCreateTime);

        Page<Carousel> page = carouselService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }

    @Operation(summary = "根据ID获取轮播图")
    @GetMapping("/{id}")
    public Result<Carousel> getById(@PathVariable Long id) {
        Carousel carousel = carouselService.getById(id);
        return Result.success(carousel);
    }

    @Operation(summary = "新增轮播图")
    @PostMapping
    public Result<Void> add(@RequestBody Carousel carousel) {
        carouselService.save(carousel);
        return Result.success();
    }

    @Operation(summary = "更新轮播图")
    @PutMapping
    public Result<Void> update(@RequestBody Carousel carousel) {
        carouselService.updateById(carousel);
        return Result.success();
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 删除数据库记录
        Carousel carousel = carouselService.getById(id);
        if (carousel != null) {
            // 删除本地图片文件
            String imgUrl = carousel.getImgUrl();
            if (imgUrl != null && !imgUrl.isEmpty()) {
                File file = new File(imgUrl);
                if (file.exists()) {
                    file.delete();
                }
            }
            carouselService.removeById(id);
        }
        return Result.success();
    }

    @Operation(summary = "更新轮播图状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Carousel carousel = new Carousel();
        carousel.setId(id);
        carousel.setStatus(status);
        carouselService.updateById(carousel);
        return Result.success();
    }

    @Operation(summary = "上传轮播图图片")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }

        try {
            // 确保上传目录存在
            File uploadDir = new File(UPLOAD_PATH);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成新文件名（使用UUID避免重复）
            String newFileName = UUID.randomUUID().toString().replace("-", "") + extension;
            String filePath = UPLOAD_PATH + newFileName;

            // 保存文件
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());

            // 返回相对路径（用于访问静态资源）
            String relativePath = "/upload_img/Carousel/" + newFileName;
            return Result.success(relativePath);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("文件上传失败：" + e.getMessage());
        }
    }

    @Operation(summary = "批量更新排序")
    @PutMapping("/sort")
    public Result<Void> updateSort(@RequestBody List<Carousel> carousels) {
        carouselService.updateBatchById(carousels);
        return Result.success();
    }
}
