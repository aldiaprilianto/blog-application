package com.application.blog.controller;

import com.application.blog.entity.Blog;
import com.application.blog.repository.BlogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlogRepository blogRepository;

    private Blog blog;

    @BeforeEach
    void setUp() {
        blog = new Blog("title1", "body1", "author1");
        blog.setId("1");
    }

    @Test
    @DisplayName("Test Success Get All Blogs")
    void getAllBlogsSuccess() throws Exception {
        // Given
        List<Blog> blogList = Arrays.asList(
                new Blog("title1", "body1", "author1"),
                new Blog("title2", "body2", "author2")
        );

        Page<Blog> blogPage = new PageImpl<>(blogList);

        when(blogRepository.findAll(any(Pageable.class))).thenReturn(blogPage);

        // When
        ResultActions response = mockMvc.perform(get("/api/blogs")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Test Success Get Blog by Id")
    void getBlogByIdSuccess() throws Exception {
        // Given
        when(blogRepository.findById("1")).thenReturn(Optional.of(blog));

        // When
        ResultActions response = mockMvc.perform(get("/api/blogs/1")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("title1"))
                .andExpect(jsonPath("$.body").value("body1"))
                .andExpect(jsonPath("$.author").value("author1"));
    }

    @Test
    @DisplayName("Test Not Found Get Blog by Id")
    void getBlogByIdNotFound() throws Exception {
        // Given
        when(blogRepository.findById("2")).thenReturn(Optional.empty());

        // When
        ResultActions response = mockMvc.perform(get("/api/blogs/2")
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test Success Update Blog by Id")
    void updateBlogSuccess() throws Exception {
        // Given
        when(blogRepository.findById("1")).thenReturn(Optional.of(blog));
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);

        Blog updatedBlog = new Blog("updatedTitle", "updatedBody", "updatedAuthor");

        // When
        ResultActions response = mockMvc.perform(put("/api/blogs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBlog)));

        // Then
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("updatedTitle"))
                .andExpect(jsonPath("$.body").value("updatedBody"))
                .andExpect(jsonPath("$.author").value("updatedAuthor"));
    }

    @Test
    @DisplayName("Test Not Found Update Blog by Id")
    void updateBlogNotFound() throws Exception {
        // Given
        when(blogRepository.findById("2")).thenReturn(Optional.empty());

        Blog updatedBlog = new Blog("updatedTitle", "updatedBody", "updatedAuthor");

        // When
        ResultActions response = mockMvc.perform(put("/api/blogs/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBlog)));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test Success Delete Blog by Id")
    void deleteBlogSuccess() throws Exception {
        // Given
        when(blogRepository.existsById("1")).thenReturn(true);

        // When
        ResultActions response = mockMvc.perform(delete("/api/blogs/1"));

        // Then
        response.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Test Not Found Delete Blog by Id")
    void deleteBlogNotFound() throws Exception {
        // Given
        when(blogRepository.existsById("2")).thenReturn(false);

        // When
        ResultActions response = mockMvc.perform(delete("/api/blogs/2"));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test Success Create Blog")
    void createBlogSuccess() throws Exception {
        // Given
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);

        // When
        ResultActions response = mockMvc.perform(post("/api/blogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blog)));

        // Then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("title1"))
                .andExpect(jsonPath("$.body").value("body1"))
                .andExpect(jsonPath("$.author").value("author1"));
    }

    @Test
    @DisplayName("Test Failed Create Blog")
    void createBlogFailed() throws Exception {
        // Given
        when(blogRepository.save(any(Blog.class))).thenReturn(blog);

        // When
        ResultActions response = mockMvc.perform(post("/api/blogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(null)));

        // Then
        response.andExpect(status().isBadRequest());
    }
}
