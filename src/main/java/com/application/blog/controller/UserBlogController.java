package com.application.blog.controller;

import com.application.blog.entity.UserBlog;
import com.application.blog.repository.UserBlogRepository;
import com.application.blog.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserBlogController {

    @Autowired
    private UserBlogRepository userBlogRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserBlog userBlog) {
        userBlogRepository.save(userBlog);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserBlog userBlog) {

        UserBlog getUser = userBlogRepository.findByUsername(userBlog.getUsername());

        if (getUser != null && getUser.getPassword().equals(userBlog.getPassword())) {
            return ResponseEntity.ok("JWT Token:"+JwtUtil.GenerateToken(getUser.getUsername()));
        } else {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = JwtUtil.ExtractToken(authorizationHeader);

        if (token != null) {
            JwtUtil.blacklistToken(token);
            return new ResponseEntity<>("Logout successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid token", HttpStatus.BAD_REQUEST);
        }
    }

}
