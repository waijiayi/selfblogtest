package com.example.selfblog.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security配置类
 * 负责配置Web应用的安全策略，包括URL访问权限控制、密码加密等
 */
@Configuration // 声明这是一个配置类，由Spring容器管理
public class Config extends WebSecurityConfigurerAdapter { // 继承Web安全配置适配器

    /**
     * 配置HTTP请求的安全策略
     * 定义不同URL路径的访问权限规则
     */
    @Override // 重写configure方法，配置HTTP安全规则
    protected void configure(HttpSecurity http) throws Exception {
        // 配置HTTP请求的访问权限
        http.authorizeRequests()
                // 允许所有用户（无需认证）
        // 以下接口允许所有用户（无需认证）
                .antMatchers("/js/**", "/login.html", "/login/**", "/").permitAll()
                .and().authorizeRequests().antMatchers(HttpMethod.GET,"/blog/*").permitAll()
                .and().authorizeRequests().antMatchers(HttpMethod.GET, "publish_blog.html").hasAuthority("ADMIN")
                .and().authorizeRequests().antMatchers(HttpMethod.PUT, "/blog").hasAuthority("ADMIN")
                .and().authorizeRequests().anyRequest().authenticated();


        http.csrf().disable(); // 禁用CSRF（跨站请求伪造）保护（通常不推荐禁用，除非有特殊要求）
    }
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    // 定义TokenFilter Bean，用于JWT认证（TokenFilter是自定义的过滤器）
    @Bean
    public TokenFilter jwtAuthenticationTokenFilter() { return new TokenFilter(); }
}