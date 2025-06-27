package com.example.selfblog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.sql.Timestamp;

@Data // Lombok 注解，自动生成 getter、setter、toString、equals 和 hashCode 方法
@AllArgsConstructor // Lombok 注解，自动生成包含所有字段的构造方法
@NoArgsConstructor // Lombok 注解，自动生成无参构造方法
@Entity // JPA 注解，表示该类是一个实体类，对应数据库中的一张表
@Table(name = "blogs") // JPA 注解，指定实体类对应的数据库表名为 "blogs"
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // JPA 注解，指定主键生成策略为自增（数据库自动生成）
    private Integer id;

    @Column(columnDefinition = "varchar(200)") // JPA 注解，指定字段的数据库列类型为 varchar(200)
    private String title;

    private Timestamp publishTime; // 博客的发布时间（使用 sql 的 Timestamp 类型）

    @Column(columnDefinition = "text") // JPA 注解，指定字段的数据库列类型为 text（长文本）
    private String content;

    private Integer scanCount; // 博客的浏览数
    private Integer likeCount; // 博客的点赞数
}