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
        
        // 3. 查询或创建用户（C端用户不设置租户ID）
        User user = userService.getByMobile(request.getMobile());
        
        if (user == null) {
            // 新用户，注册账号
            user = userService.createUser(request.getMobile(), null);
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
        // 微信小程序登录流程：
        // 1. 使用code调用微信接口换取openid和session_key
        // 2. 根据openid查询或创建用户
        // 3. 更新用户信息（昵称、头像等）
        // 4. 使用Sa-Token进行登录
        
        try {
            // 1. 调用微信接口获取openid
            // 实际项目中需要集成微信API：
            // String openid = getWxOpenid(request.getCode());
            // 开发环境使用code作为模拟openid
            String openid = "wx_" + request.getCode();
            
            if (openid == null) {
                return Result.fail("微信登录失败");
            }
            
            // 2. 查询或创建用户
            Long tenantId = request.getTenantId() != null ? request.getTenantId() : 1L;
            // 使用UserServiceImpl中的getByOpenidAndTenant方法
            User user = ((com.linlee.cloudsnow.module.user.service.impl.UserServiceImpl) userService)
                    .getByOpenidAndTenant(openid, tenantId);
            
            if (user == null) {
                // 新用户，创建账号（C端用户不设置租户ID）
                user = new User();
                user.setOpenid(openid);
                user.setNick(request.getNickName() != null ? request.getNickName() : "微信用户");
                user.setAvatar(request.getAvatarUrl());
                user.setTenantId(null);
                user.setStatus(1);
                user.setVipLevel(0);
                user.setPoints(0);
                userService.save(user);
            } else {
                // 更新用户信息
                if (request.getNickName() != null) {
                    user.setNick(request.getNickName());
                }
                if (request.getAvatarUrl() != null) {
                    user.setAvatar(request.getAvatarUrl());
                }
                userService.updateById(user);
            }
            
            // 3. 使用Sa-Token进行登录
            StpUtil.login(user.getUserId());
            
            // 4. 返回登录信息
            LoginResponse response = new LoginResponse();
            response.setToken(StpUtil.getTokenValue());
            response.setUserId(user.getUserId());
            response.setNick(user.getNick());
            response.setAvatar(user.getAvatar());
            response.setTenantId(user.getTenantId());
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("微信登录失败", e);
            return Result.fail("微信登录失败");
        }
        
        // 实际集成微信API的示例代码：
        /*
        private String getWxOpenid(String code) {
            String appId = "your_app_id";
            String appSecret = "your_app_secret";
            String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, code
            );
            
            // 使用RestTemplate或HttpClient调用微信API
            String response = restTemplate.getForObject(url, String.class);
            JSONObject json = JSON.parseObject(response);
            return json.getString("openid");
        }
        */
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
