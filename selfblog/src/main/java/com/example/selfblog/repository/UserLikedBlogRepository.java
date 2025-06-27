package com.example.selfblog.repository;

import com.example.selfblog.entity.UserLikedBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户点赞博客关联数据访问层
 * 负责管理用户与博客的点赞关系
 */
public interface UserLikedBlogRepository extends JpaRepository<UserLikedBlog, Integer>,
        JpaSpecificationExecutor<UserLikedBlog> {
    @Transactional
    @Modifying
    @Query(nativeQuery = true,
            value = "delete from user_liked_blog where blog_id=?1 and user_id=?2 ")
    int delUserLikedBlogs(int blogId, int userId);  // ?1和?2对应方法参数
    int countByBlogIdAndUserId(int blogId,int userId);
}