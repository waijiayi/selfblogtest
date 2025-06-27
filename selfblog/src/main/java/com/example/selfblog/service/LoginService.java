package com.example.selfblog.service;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录服务接口
 * 定义用户认证和会话管理的核心方法
 */
public interface LoginService {
    /**
     * 密码登录方式
     * @param phoneNumber 用户手机号（登录凭证）
     * @param password 明文密码（需与数据库加密密码比对）
     * @param response 用于设置登录成功时的Cookie或响应头
     * @return 登录结果：
     *          - 成功：返回JWT令牌字符串
     *          - 失败：返回错误码（如"0"表示验证失败）
     */
    String loginByPW(String phoneNumber, String password, HttpServletResponse response);

    /**
     * 手机号一键登录/注册
     * 1. 若用户存在：直接登录
     * 2. 若用户不存在：自动注册并登录
     * @param phoneNumber 用户手机号
     * @param response 用于设置登录成功时的Cookie或响应头
     * @return 登录结果：
     *          - 成功：返回JWT令牌字符串
     *          - 失败：返回错误码
     */
    String loginOrRegister(String phoneNumber,HttpServletResponse response);
}