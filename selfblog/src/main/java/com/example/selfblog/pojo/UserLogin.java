package com.example.selfblog.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 自动生成getter和setter方法
@AllArgsConstructor // 通过所有参数生成构造函数
@NoArgsConstructor // 无参构造函数
public class UserLogin {
    private String phoneNumber;
    private String verifyCode;
    private String password;
}