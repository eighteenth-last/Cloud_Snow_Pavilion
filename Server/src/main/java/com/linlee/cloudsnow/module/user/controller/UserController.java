package com.linlee.cloudsnow.module.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.user.entity.User;
import com.linlee.cloudsnow.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-12-17
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.success(user);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody User user) {
        Long userId = StpUtil.getLoginIdAsLong();
        user.setUserId(userId);
        
        // 只允许更新部分字段
        User updateUser = new User();
        updateUser.setUserId(userId);
        updateUser.setNick(user.getNick());
        updateUser.setAvatar(user.getAvatar());
        updateUser.setMobile(user.getMobile());
        
        userService.updateById(updateUser);
        return Result.success();
    }
}
