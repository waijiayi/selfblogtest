package com.example.selfblog.repository;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * 短信验证码服务
 * 提供生成和验证短信验证码的功能
 * 注意：当前使用内存Map存储验证码，仅用于演示，实际生产环境应使用Redis或数据库
 */
@Component
public class SmsService {

    /**
     * 模拟存储，在实际项目中可以用数据库或者Redis等来存储。
     * 键：手机号
     * 值：验证码
     */
    private static Map<String, String> vcMap = new HashMap<>();

    /**
     * 生成验证码，并以手机号为key存储起来，然后返回生成的验证码。
     * @param phoneNumber 手机号
     * @return 生成的验证码
     */
    public String genVerifyCode(String phoneNumber) {
        String vc = "123456"; // 这里为了测试方便，写为123456。
        vcMap.put(phoneNumber, vc); // 存储验证码到内存Map
        return vc;
    }
    /**
     * 校验用户传递的验证码是否正确。返回true标识配置，false表示不匹配。
     * 校验用户传递的验证码是否正确。
     * 验证成功后会删除已存储的验证码，防止重复使用
     */
    public boolean checkVerifyCode(String phoneNumber, String verifyCode) {
        String vc = vcMap.get(phoneNumber); // 获取存储的验证码
        if (vc == null) {
            return false;
        } else {
            if(vc.equals(verifyCode)){
                vcMap.remove(phoneNumber); // 验证成功，删除已使用的验证码
                return true;
            }else {
                return false;
            }
        }
    }
}