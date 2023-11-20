package com.application.blog.controller;
import com.application.blog.entity.Blog;
import com.application.blog.repository.BlogRepository;
import com.application.blog.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    @Autowired
    private BlogRepository blogRepository;

    @GetMapping
    public ResponseEntity<Page<Blog>> getAllBlogs(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        String token = JwtUtil.ExtractToken(authorizationHeader);

        if (!JwtUtil.ValidateToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        Page<Blog> blogs = blogRepository.findAll(pageable);

        return new ResponseEntity<>(blogs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader,@PathVariable String id) {

        String token = JwtUtil.ExtractToken(authorizationHeader);

        if (!JwtUtil.ValidateToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Blog blog = blogRepository.findById(id).orElse(null);
        if (blog != null) {
            return new ResponseEntity<>(blog, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Blog> createBlog(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody Blog blog) {

        String token = JwtUtil.ExtractToken(authorizationHeader);

        if (!JwtUtil.ValidateToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        blog.setAuthor(JwtUtil.GetUsernameFromToken(token));

        Blog createdBlog = blogRepository.save(blog);
        return new ResponseEntity<>(createdBlog, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Blog> updateBlog(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable String id, @RequestBody Blog updatedBlog) {

        String token = JwtUtil.ExtractToken(authorizationHeader);

        if (!JwtUtil.ValidateToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Blog existingBlog = blogRepository.findById(id).orElse(null);
        if (existingBlog != null) {
            existingBlog.setTitle(updatedBlog.getTitle());
            existingBlog.setBody(updatedBlog.getBody());
            existingBlog.setAuthor(JwtUtil.GetUsernameFromToken(token));
            Blog savedBlog = blogRepository.save(existingBlog);
            return new ResponseEntity<>(savedBlog, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable String id) {

        String token = JwtUtil.ExtractToken(authorizationHeader);

        if (!JwtUtil.ValidateToken(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (blogRepository.existsById(id)) {
            blogRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
