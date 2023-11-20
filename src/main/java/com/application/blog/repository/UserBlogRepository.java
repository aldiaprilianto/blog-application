package com.application.blog.repository;

import com.application.blog.entity.UserBlog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBlogRepository extends JpaRepository<UserBlog, Integer> {
    UserBlog findByUsername(String username);
}
