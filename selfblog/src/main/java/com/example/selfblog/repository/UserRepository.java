package com.example.selfblog.repository;

import com.example.selfblog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 用户数据访问层接口
 * 负责与数据库进行交互，处理用户实体的CRUD操作
 * Spring Data JPA会自动生成实现类
 */
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    /**
     * 根据手机号查询用户
     * 等效SQL: SELECT * FROM user WHERE phone_number = ?
     * @param phoneNumber 用户手机号
     * @return 匹配的用户实体，若不存在则返回null
     */
    User findOneByPhoneNumber(String phoneNumber);
}