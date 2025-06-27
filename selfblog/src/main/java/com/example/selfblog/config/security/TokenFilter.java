package com.example.selfblog.config.security;

import com.example.selfblog.entity.User;
import com.example.selfblog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT令牌认证过滤器
 * 负责从请求中提取JWT令牌并验证，将用户信息存入安全上下文。用于维护用户的登录状态
 * 认证流程：提取令牌 -> 2. 解析用户 ID -> 3. 查询用户信息 -> 4. 验证令牌有效性 -> 5. 设置安全上下文
 */
@Slf4j // 自动生成日志对象（log）0个用法
public class TokenFilter extends OncePerRequestFilter { // 继承单次请求过滤器（保证每个请求只过滤一次
    @Autowired // 自动注入令牌服务
    TokenService tokenService;
    @Autowired // 自动注入用户数据访问层
    UserRepository userRepository;

    /**
     * 核心过滤方法
     * 处理请求认证逻辑
     */
    @Override protected void doFilterInternal(HttpServletRequest request,
                                              HttpServletResponse response,
                                              FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request); // 从请求中提取令牌
        //String token = request.getHeader("token");
        // 检查令牌非空且非空白
        if(token!=null && !"".equals(token.trim())){ // 排除空令牌和仅含空格的令牌
            Integer userId = tokenService.getUserIdFromToken(token); // 从令牌中提取用户ID

            // 用户ID存在且当前安全上下文未认证

            if (null != userId && null == SecurityContextHolder.getContext().getAuthentication()) {
                User user = null;
                try {
                    user = userRepository.getById(userId);// 根据用户ID查询数据库
                } catch (JpaObjectRetrievalFailureException e) {
                    log.error("token中的userId无效" + userId, e);
                    throw new RuntimeException("token中的userId无效" + userId);
                }
                // 验证用户存在且令牌有效

            if (user != null && tokenService.validateToken(token, user)) {
                    // 创建认证令牌
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(user,  null, user.getAuthorities());
                    // 设置认证详情（如IP地址、请求时间等）
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 将认证信息存入安全上下文
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    log.error("登录状态失效，请重新登陆。");
                    throw new RuntimeException("登录状态失效，请重新登陆。");
                }
            }
        }
        filterChain.doFilter(request, response); // 将请求传递给下一个过滤器
    }

    /**
     * 从请求中提取令牌（优先从Cookie获取）
     */
    private String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();// 获取请求中的所有Cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {// 匹配名为"token"的Cookie
                    return cookie.getValue(); // 返回令牌值
                }
            }
        }
        return null;
    }
}