package com.linlee.cloudsnow.module.common.controller;

import com.linlee.cloudsnow.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "文件上传")
@RestController
@RequestMapping("/file")
public class FileController {

    private static final String UPLOAD_DIR = "R:\\Code_Repository\\Cloud_Snow_Pavilion\\upload_img";
    
    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail("请选择要上传的文件");
        }

        try {
            // 确保上传目录存在
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = Paths.get(UPLOAD_DIR, fileName);

            // 保存文件
            Files.write(filePath, file.getBytes());

            // 返回文件访问路径（相对路径）
            String fileUrl = "/upload_img/" + fileName;
            
            return Result.success("上传成功", fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("文件上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取图片")
    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
            Resource resource = new FileSystemResource(filePath);
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // 获取文件的 MIME 类型
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
