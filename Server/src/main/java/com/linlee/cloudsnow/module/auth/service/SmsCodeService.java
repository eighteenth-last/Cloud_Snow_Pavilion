package com.linlee.cloudsnow.module.auth.service;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 *
 * @author Cloud Snow Pavilion
 * @since 2025-11-22
 */
@Slf4j
@Service
public class SmsCodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRE_MINUTES = 5;

    /**
     * 生成并发送验证码
     */
    public String generateCode(String mobile) {
        // 生成6位随机数字验证码
        String code = RandomUtil.randomNumbers(CODE_LENGTH);
        
        // 存储到Redis，5分钟过期
        String key = SMS_CODE_PREFIX + mobile;
        stringRedisTemplate.opsForValue().set(key, code, EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        log.info("生成验证码：手机号={}, 验证码={}", mobile, code);
        
        // TODO: 实际项目中这里需要调用短信服务发送验证码
        // 开发环境直接返回验证码方便测试
        return code;
    }

    /**
     * 验证验证码
     */
    public boolean verifyCode(String mobile, String code) {
        String key = SMS_CODE_PREFIX + mobile;
        String savedCode = stringRedisTemplate.opsForValue().get(key);
        
        if (savedCode == null) {
            log.warn("验证码已过期或不存在：手机号={}", mobile);
            return false;
        }
        
        boolean result = savedCode.equals(code);
        
        if (result) {
            // 验证成功后删除验证码
            stringRedisTemplate.delete(key);
            log.info("验证码验证成功：手机号={}", mobile);
        } else {
            log.warn("验证码验证失败：手机号={}, 输入={}, 正确={}", mobile, code, savedCode);
        }
        
        return result;
    }
}
