package com.example.selfblog.controller;

import com.example.selfblog.entity.User;
import com.example.selfblog.pojo.UserLogin;
import com.example.selfblog.repository.SmsService;
import com.example.selfblog.repository.UserRepository;
import com.example.selfblog.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 * 处理用户个人页面和密码修改等操作
 */
@Controller // 声明为Spring MVC控制器
@RequestMapping("user") // 定义统一的请求路径前缀
public class UserController {

    @Autowired // 自动注入短信服务
    SmsService smsService;
    @Autowired // 自动注入密码编码器
    PasswordEncoder passwordEncoder;
    @Autowired // 自动注入用户数据访问层
    UserRepository userRepository;

    /**
     * 使用验证码修改密码
     * @param userLogin 包含验证码和新密码的请求体
     * @return 操作结果："1"成功，"0"失败
     */
    @ResponseBody // 返回JSON响应而非视图
    @PostMapping("chpw") // 处理POST请求，路径"/user/chpw"
    public String setPasswordByVerifyCode(@RequestBody UserLogin userLogin) {
        User user = UserUtil.getUser(); // 获取当前登录用户

        // 验证验证码是否正确
        if (smsService.checkVerifyCode(user.getPhoneNumber(), userLogin.getVerifyCode())) {
            // 加密新密码并更新到数据库
            user.setPassword(passwordEncoder.encode(userLogin.getPassword()));
            userRepository.save(user); // 保存更新
            return "1";
        } else {
            return "0";
        }
    }
    @GetMapping
    public String userIndex(Map<String, Object>map){
        map.put("user",UserUtil.getUser());
        return "self_page";
    }
}