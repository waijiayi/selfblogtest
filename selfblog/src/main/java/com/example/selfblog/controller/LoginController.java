package com.example.selfblog.controller;

import com.example.selfblog.pojo.UserLogin;
import com.example.selfblog.repository.SmsService;
import com.example.selfblog.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 登录控制器
 * 处理用户登录相关的HTTP请求，包括短信验证码登录和密码登录
 */
@RestController // 声明为RESTful风格控制器，返回JSON数据而非视图
@RequestMapping("login") // 定义统一的请求路径前缀
public class LoginController {

    @Autowired // 自动注入短信服务Bean
    SmsService smsService;
    @Autowired
    LoginService loginService;

    //@Autowired // 自动注入登录业务逻辑Bean
    //LoginService loginService;

    /**
     * 发送短信验证码接口
     * 接收手机号，生成验证码并存储，返回成功状态
     * @param userLogin 包含手机号的请求体
     * @return "1"表示成功（简化设计，实际应返回更详细信息）
     */
    @PutMapping("sms")
    // @RequestBody注解表示从请求体中读取数据，而不是从URL中获取参数。它会将前端发来的数据反序列化为userLogin对象
    public String sendSms(@RequestBody UserLogin userLogin){
        String verifyCode = smsService.genVerifyCode(userLogin.getPhoneNumber());
        return "1";
    }
    /**
     * @return 登录结果：成功返回token或用户信息，失败返回"0"
     */
    @PostMapping("byvc")
// HttpServletResponse 参数用于处理 HTTP 响应。
// 当客户端（例如，浏览器）发送一个 HTTP 请求到服务器时, 服务器会处理这个请求, 并生成一个 HTTP 响应。
// HttpServletResponse 对象代表了这个响应，它允许服务器设置响应头、状态码、Cookie，以及输出响应体。
// HttpServletResponse response 参数是从客户端请求的上下文中传递来的
    public String loginByVerifyCode(@RequestBody UserLogin userLogin, HttpServletResponse response){
        boolean flag = smsService.checkVerifyCode(userLogin.getPhoneNumber(), userLogin.getVerifyCode());
        if(flag){
            return loginService.loginOrRegister(userLogin.getPhoneNumber(),response);
        }else{
            return "0";
        }
    }
    /**
     * @return 登录结果：成功返回token或用户信息，失败返回错误信息
     */
    @PostMapping("bypw")
    public String loginByPassword(@RequestBody UserLogin userLogin, HttpServletResponse response){
        return loginService.loginByPW(userLogin.getPhoneNumber(), userLogin.getPassword(), response);
    }
}