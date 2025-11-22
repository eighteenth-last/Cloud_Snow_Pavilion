package com.linlee.cloudsnow.module.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.linlee.cloudsnow.common.context.TenantContext;
import com.linlee.cloudsnow.common.exception.BusinessException;
import com.linlee.cloudsnow.common.result.Result;
import com.linlee.cloudsnow.module.auth.dto.LoginRequest;
import com.linlee.cloudsnow.module.auth.dto.LoginResponse;
import com.linlee.cloudsnow.module.auth.dto.RegisterRequest;
import com.linlee.cloudsnow.module.auth.dto.WxLoginRequest;
import com.linlee.cloudsnow.module.auth.service.SmsCodeService;
import com.linlee.cloudsnow.module.staff.entity.Staff;
import com.linlee.cloudsnow.module.staff.service.StaffService;
import com.linlee.cloudsnow.module.tenant.entity.Tenant;
import com.linlee.cloudsnow.module.tenant.service.TenantService;
import com.linlee.cloudsnow.module.user.entity.User;
import com.linlee.cloudsnow.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static cn.dev33.satoken.SaManager.log;

/**
 * 认证Controller
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Tag(name = "用户认证")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SmsCodeService smsCodeService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private StaffService staffService;
    
    @Autowired
    private TenantService tenantService;

    @Operation(summary = "获取短信验证码")
    @PostMapping("/getSmsCode")
    public Result<String> getSmsCode(@RequestParam String mobile) {
        // 生成并发送验证码
        String code = smsCodeService.generateCode(mobile);
        log.info("验证码: " + code);
        // 开发环境返回验证码，生产环境应该不返回
        return Result.success("验证码发送成功（开发环境）: " + code);
    }

    @Operation(summary = "商家注册")
    @PostMapping("/admin/register")
    public Result<LoginResponse> adminRegister(@RequestBody RegisterRequest request) {
        // 1. 验证必填字段
        if (request.getTenantName() == null || request.getTenantName().trim().isEmpty()) {
            throw new BusinessException("店铺名称不能为空");
        }
        if (request.getMobile() == null || request.getMobile().trim().isEmpty()) {
            throw new BusinessException("手机号不能为空");
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BusinessException("登录账号不能为空");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BusinessException("姓名不能为空");
        }
        
        // 2. 检查用户名是否已存在
        Staff existingStaff = staffService.getByUsername(request.getUsername());
        if (existingStaff != null) {
            throw new BusinessException("该账号已被注册");
        }
        
        // 3. 创建租户
        Tenant tenant = new Tenant();
        tenant.setName(request.getTenantName());
        tenant.setStatus(1);
        tenantService.save(tenant);
        
        Long tenantId = tenant.getTenantId();
        
        // 4. 创建管理员账号
        Staff staff = new Staff();
        staff.setTenantId(tenantId);
        staff.setUsername(request.getUsername());
        // 使用BCrypt加密密码
        staff.setPassword(staffService.encodePassword(request.getPassword()));
        staff.setName(request.getName());
        staff.setPhone(request.getMobile());
        staff.setPosition("创始人");
        staff.setStatus(1);
        staffService.save(staff);
        
        // 5. 自动登录
        StpUtil.login("admin:" + staff.getStaffId());
        
        // 6. 返回登录信息
        LoginResponse response = new LoginResponse();
        response.setToken(StpUtil.getTokenValue());
        response.setUserId(staff.getStaffId());
        response.setNick(staff.getName());
        response.setTenantId(tenantId);
        
        return Result.success(response);
    }

    @Operation(summary = "后台管理登录（租户/老板账号密码登录）")
    @PostMapping("/admin/login")
    public Result<LoginResponse> adminLogin(@RequestBody LoginRequest request) {
        // 1. 验证账号
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BusinessException("账号不能为空");
        }
        
        // 2. 验证密码
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BusinessException("密码不能为空");
        }
        
        // 3. 查询租户管理员（老板账号）
        // 登录时需要忽略租户过滤，因为此时还没有设置租户ID
        TenantContext.setIgnore(true);
        Staff staff;
        try {
            staff = staffService.getByUsername(request.getUsername());
        } finally {
            TenantContext.setIgnore(false);
        }
        
        if (staff == null) {
            throw new BusinessException("账号不存在");
        }
        
        // 4. 验证密码
        if (!staffService.verifyPassword(request.getPassword(), staff.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        // 5. 使用Sa-Token进行登录（使用staff_id，加上admin:前缀区分B端老板和C端用户）
        StpUtil.login("admin:" + staff.getStaffId());
        
        // 6. 返回登录信息
        LoginResponse response = new LoginResponse();
        response.setToken(StpUtil.getTokenValue());
        response.setUserId(staff.getStaffId());
        response.setNick(staff.getName());
        response.setTenantId(staff.getTenantId());
        
        return Result.success(response);
    }

    @Operation(summary = "手机号验证码登录（C端用户）")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        // 1. 验证手机号
        log.info("手机号: " + request.getMobile());
        if (request.getMobile() == null || request.getMobile().trim().isEmpty()) {
            throw new BusinessException("手机号不能为空");
        }
        
        // 2. 验证验证码
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            throw new BusinessException("验证码不能为空");
        }
        
        if (!smsCodeService.verifyCode(request.getMobile(), request.getCode())) {
            throw new BusinessException("验证码错误或已过期");
        }
        
        // 3. 查询或创建用户
        User user = userService.getByMobile(request.getMobile());
        if (user == null) {
            // 新用户，注册账号
            Long tenantId = request.getTenantId() != null ? request.getTenantId() : 1L;
            user = userService.createUser(request.getMobile(), tenantId);
        }
        
        // 4. 使用Sa-Token进行登录
        StpUtil.login(user.getUserId());
        
        // 5. 返回登录信息
        LoginResponse response = new LoginResponse();
        response.setToken(StpUtil.getTokenValue());
        response.setUserId(user.getUserId());
        response.setNick(user.getNick());
        response.setAvatar(user.getAvatar());
        response.setTenantId(user.getTenantId());
        
        return Result.success(response);
    }

    @Operation(summary = "微信小程序登录")
    @PostMapping("/wxLogin")
    public Result<LoginResponse> wxLogin(@RequestBody WxLoginRequest request) {
        // TODO: 实现微信登录逻辑
        // 1. 使用code换取openid
        // 2. 查询或创建用户
        // 3. 使用Sa-Token进行登录
        
        // 模拟登录成功
        Long userId = 1L;
        StpUtil.login(userId);
        
        LoginResponse response = new LoginResponse();
        response.setToken(StpUtil.getTokenValue());
        response.setUserId(userId);
        response.setNick("微信用户");
        response.setTenantId(request.getTenantId());
        
        return Result.success(response);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.success();
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping("/getUserInfo")
    public Result<LoginResponse> getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        
        LoginResponse response = new LoginResponse();
        response.setUserId(userId);
        response.setNick("当前用户");
        response.setTenantId(1L);
        
        return Result.success(response);
    }

    @Operation(summary = "检查登录状态")
    @GetMapping("/checkLogin")
    public Result<Boolean> checkLogin() {
        boolean isLogin = StpUtil.isLogin();
        return Result.success(isLogin);
    }
}
