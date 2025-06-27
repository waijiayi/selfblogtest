package com.example.selfblog.service.impl;
import com.example.selfblog.config.security.TokenService;
import com.example.selfblog.entity.User;
import com.example.selfblog.repository.UserRepository;
import com.example.selfblog.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录服务实现类
 * 处理用户登录、注册及认证信息管理
 */
@Service // 声明为Spring服务组件
public class LoginServiceImpl implements LoginService {
    @Autowired // 自动注入密码编码器
    PasswordEncoder passwordEncoder;
    @Autowired // 自动注入令牌服务
    TokenService tokenService;
    @Autowired // 自动注入用户数据访问层
    UserRepository userRepository;

    /**
     * 手机号登录/注册方法
     * 验证手机号，若用户不存在则自动注册
     */
    @Override
    public String loginOrRegister(String phoneNumber, HttpServletResponse response) {
        User user = userRepository.findOneByPhoneNumber(phoneNumber);
        if (null == user) { // 用户不存在，需要先创建用户
            user = new User();
            user.setPhoneNumber(phoneNumber);
            user.setPassword(""); // 初始密码为空
            Long currentTimeStamp = System.currentTimeMillis();
            user.setUpdateTime(new Timestamp(currentTimeStamp));
            userRepository.save(user); // 保存新用户到Mysql数据库
        }
        return updateSecurityUserInfo(user, response); // 更新安全上下文并生成令牌
    }

    /**
     * 更新安全上下文并生成令牌
     * 将用户信息存入Security上下文并生成JWT令牌
     */
    public String updateSecurityUserInfo(User user, HttpServletResponse response) {
        // 创建一个 UsernamePasswordAuthenticationToken 对象，用于用户认证
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        // 设置认证信息到安全上下文中
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // 生成JWT令牌
        String token = tokenService.generateToken(user);
        // 创建HTTP Cookie存储令牌
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true); // 防止 XSS 攻击
        cookie.setPath("/"); // 设置 Cookie 的路径为根路径，使其在整个网站范围内有效。
        response.addCookie(cookie);
        return token; // 返回令牌供前端使用
    }
    @Override
    public String loginByPW(String phoneNumber, String password, HttpServletResponse response) {
        // 根据手机号查询用户
        User user = userRepository.findOneByPhoneNumber(phoneNumber);
        // 验证用户存在且密码匹配
        if (null == user || !passwordEncoder.matches(password, user.getPassword())) {
            return "0";
        }
        // 更新安全上下文并生成令牌
        return updateSecurityUserInfo(user, response);
    }
}