package com.example.selfblog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_liked_blog")
public class UserLikedBlog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // JPA注解，指定主键生成策略为自增
    private Integer id;

    @ManyToOne(optional = true) // JPA注解，声明多对一关联关系，optional=true表示可以为空
    private Blog blog;

    @ManyToOne(optional = true)
    private User user;

    private Timestamp likeTime;
}