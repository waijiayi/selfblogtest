package com.example.selfblog.config.security;

import com.example.selfblog.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT令牌服务
 * 负责生成、解析和验证JSON Web Token
 */
@Component // 声明为Spring组件，由容器管理实例
public class TokenService {

    private static final String SUB = "sub"; // JWT标准字段：主题(Subject)

    /**
     * 1.根据用户信息生成JWT令牌
     * @param user 用户实体
     * @return 生成的JWT字符串
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>(); // 创建一个 HashMap 对象（载荷Map），用于存储 JWT 的声明。
        claims.put(SUB, user.getId()); // 将用户 ID 存入声明中，键为 SUB。
        return generateToken(claims); // 调用私有方法生成令牌
    }

    /**
     * 根据载荷生成JWT令牌
     * @param claims 令牌载荷
     * @return JWT字符串
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder() // 创建JWT构建器
                .setClaims(claims) // 设置自定义载荷
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 3600000L)) // 设置过期时间（7天）
                .signWith(SignatureAlgorithm.HS256, "secret") // 使用HS256算法和密钥签名
                .compact(); // 生成最终的JWT字符串
    }

    /**
     * 2.从JWT令牌(token)中提取用户ID
     * @param token JWT字符串
     * @return 用户ID，解析失败返回null
     */
    public Integer getUserIdFromToken(String token) {
        String userId;
        try {
            Claims claims = getClaimsFromToken(token); // 解析令牌获取载荷
            userId = claims.getSubject(); // 从主题字段获取用户ID
        } catch (Exception e) {
            return null;
        }
        return Integer.valueOf(userId); // 转换为Integer类型
    }

    /**
     * 从JWT令牌中解析载荷
     * @param token JWT字符串
     * @return 解析后的载荷对象，失败返回null
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser() // 创建JWT解析器
                    .setSigningKey("secret") // 设置密钥（需与签名时一致）
                    .parseClaimsJws(token) // 解析JWT
                    .getBody(); // 获取载荷部分
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }

    /**
     * 3.验证JWT令牌有效性
     * @param token JWT字符串
     * @param user 用户实体
     * @return 是否有效
     */
    public boolean validateToken(String token, User user) {
        // 验证条件：
        // 1.令牌中的用户ID与提供的用户ID匹配
        // 2.令牌未过期
        Integer userId = getUserIdFromToken(token);
        return userId.equals(user.getId())
                && !isTokenExpired(token);
    }

    /**
     * 判断令牌是否过期
     * @param token JWT字符串
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expireDate = getExpireDateFromToken(token); // 获取过期时间
        return expireDate.before(new Date()); // 比较过期时间是否早于当前时间
    }

    /**
     * 从令牌中获取过期时间
     * @param token JWT字符串
     * @return 过期时间
     */
    public Date getExpireDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token); // 解析载荷
        return claims.getExpiration(); // 获取过期时间字段
    }
}