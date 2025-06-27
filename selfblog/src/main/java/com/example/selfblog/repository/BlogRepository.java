package com.example.selfblog.repository;

import com.example.selfblog.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * 博客数据访问层接口
 * 负责博客实体的CRUD操作和自定义更新
 */
public interface BlogRepository extends JpaRepository<Blog, Integer>,
        JpaSpecificationExecutor<Blog> {
    // 继承JpaRepository提供基础CRUD功能
    // 继承JpaSpecificationExecutor提供动态查询功能
    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update blogs set scan_count = scan_count+1 where id = ?1")
    int addScanCount(int id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update blogs set like_count = like_count+1 where id = ?1")
    int addLikeCount(int id);
    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "update blogs set like_count = like_count-1 where id = ?1")
    int subLikeCount(int id);
}