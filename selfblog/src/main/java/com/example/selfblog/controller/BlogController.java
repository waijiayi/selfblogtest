package com.example.selfblog.controller;

// 导入所需的实体类
import com.example.selfblog.entity.Blog;
import com.example.selfblog.entity.User;
import com.example.selfblog.entity.UserLikedBlog;
import com.example.selfblog.repository.BlogRepository;
//import com.example.selfblog.repository.UserLikedBlogRepository;
//import com.example.selfblog.util.UserUtil;
import com.example.selfblog.repository.UserLikedBlogRepository;
import com.example.selfblog.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;
@Controller // 标记该类为Spring MVC控制器
public class BlogController {
    @Autowired // 自动注入博客数据访问层
    BlogRepository blogRepository;

    /**
     * 发布博客接口
     * 接收博客对象，设置发布时间和初始统计数据，保存到数据库
     */
    @PutMapping("blog") // 处理PUT请求，用于发布博客
    @ResponseBody // 返回JSON数据而非视图
    public String publishBlog(@RequestBody Blog blog) {
        // 设置博客的发布时间为当前时间
        blog.setPublishTime(new Timestamp(System.currentTimeMillis()));
        blog.setLikeCount(0);
        blog.setScanCount(0);
        blogRepository.save(blog); // 保存博客到数据库
        return "1"; // 返回成功状态码
    }
    @GetMapping({"blog", ""})
    public String blogs(@RequestParam(required = false, defaultValue = "0") int page,
                        @RequestParam(required = false, defaultValue = "5") int size,
                        Map<String, Object> map) {
        Page<Blog> blogs = blogRepository.findAll(PageRequest.of(page, size));
        map.put("blogs", blogs.getContent());
        map.put("pageLast", page == 0 ? 0 : page - 1);// 上一页页码
        map.put("pageNext", page + 1);
        map.put("size", size); // 每页大小
        map.put("total", blogs.getTotalPages());
        map.put("currentPage", page + 1); // 当前页码（从1开始）
        map.put("user", UserUtil.getUser()); // 当前登录用户
        return "index"; // 返回博客列表视图。前端通过map的键（如blogs、pageLast等）获取数据。
    }
    @GetMapping("blog/{id}") // 处理/blog/{id}的GET请求
    public String blog(@PathVariable int id, Map<String, Object> map) { // 从URL获取博客ID
        //Optional<Blog>, 表示查询结果可能存在或不存在
        Optional<Blog> blogOpt = blogRepository.findById(id); // 查询博客是否存在
        if (blogOpt.isEmpty()) {
            return "error";
        }
        Blog blog = blogOpt.get(); // 获取博客对象
        map.put("id", blog.getId());
        map.put("title", blog.getTitle());
        // 格式化发布时间为友好格式
        map.put("publishTime", new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒")
                .format(blog.getPublishTime()));
        map.put("scanCount", blog.getScanCount());
        map.put("likeCount", blog.getLikeCount());
        map.put("content", blog.getContent());

        User user = UserUtil.getUser(); // 获取当前登录用户
        if (user != null) {
            // 查询用户是否已点赞该博客
            int hasLiked = userLikedBlogRepository.countByBlogIdAndUserId(id, user.getId());
            if (hasLiked > 0) {  // 设置是否已点赞标记
                map.put("hasLiked", "1");
            } else {
                map.put("hasLiked", "0");
            }
        }
        return "blog";
    }
    @PostMapping("blog/{id}/scan") // 处理/blog/{id}/scan的POST请求
    @ResponseBody
    public int scan(@PathVariable int id) {
        blogRepository.addScanCount(id); // 增加浏览量
        return blogRepository.getById(id).getScanCount(); // 返回更新后的浏览数
    }
    @Autowired  // 自动注入用户点赞数据访问层
    UserLikedBlogRepository userLikedBlogRepository;

    /**
     * 博客点赞接口
     * 创建点赞记录并增加博客点赞数
     */
    @Transactional // 声明为事务方法
    @PostMapping("blog/{id}/like") // 处理/blog/{id}/like的POST请求
    @ResponseBody
    public int like(@PathVariable int id) {
        User user = UserUtil.getUser(); // 获取当前登录用户
        if (user == null) {
            return 0;
        }
        Blog blog = blogRepository.getById(id); // 获取博客对象
        if (blog == null) {
            return 0;
        }
        UserLikedBlog userLikedBlog = new UserLikedBlog();
        userLikedBlog.setUser(user);
        userLikedBlog.setBlog(blog);
        userLikedBlog.setLikeTime(new Timestamp(System.currentTimeMillis()));
        userLikedBlogRepository.save(userLikedBlog); // 保存点赞记录
        blogRepository.addLikeCount(id); // 增加博客点赞数
        return 1;
    }
    @Transactional
    @PostMapping("blog/{id}/dislike")
    @ResponseBody
    public int dislike(@PathVariable int id) {
        User user = UserUtil.getUser();
        if (user == null) {
            return 0;
        }
        userLikedBlogRepository.delUserLikedBlogs(id, user.getId()); // 删除点赞记录
        blogRepository.subLikeCount(id); // 减少博客点赞数
        return 1;
    }
}