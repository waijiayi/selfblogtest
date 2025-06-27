package com.example.selfblog.util;

import com.example.selfblog.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 用户工具类
 * 提供从安全上下文中获取当前登录用户的静态方法
 */
public class UserUtil {
    /**
     * 获取当前登录用户
     * 从Spring Security的安全上下文中提取用户信息
     * @return 当前登录用户对象，未登录则返回null
     */
    public static User getUser(){
        // 获取当前线程的认证对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 检查认证对象是否存在
        if(authentication == null){
            return null;
        }
        try {
            // 从认证对象中获取主体（principal）并强制转换为User类型
            // 主体通常是UserDetails的实现类（这里直接是User实体）
            return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e){
            return null;
        }
    }
}