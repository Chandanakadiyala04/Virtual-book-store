package com.bookstore.controller;

import com.bookstore.model.Review;
import com.bookstore.repository.ReviewRepository;
import com.bookstore.security.JwtUtils;
import com.bookstore.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@Test(groups = "controller")
public class ReviewControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Review review1;
    private Review review2;

    @BeforeMethod
    public void setUp() {
        review1 = new Review();
        review1.setId("rev1");
        review1.setBookId("book1");
        review1.setUserId("user1");
        review1.setUsername("john");
        review1.setRating(5);
        review1.setComment("Excellent book!");

        review2 = new Review();
        review2.setId("rev2");
        review2.setBookId("book1");
        review2.setUserId("user2");
        review2.setUsername("jane");
        review2.setRating(4);
        review2.setComment("Great read!");
    }

    @WithMockUser
    public void testGetReviewsByBook() throws Exception {
        List<Review> reviews = Arrays.asList(review1, review2);
        when(reviewRepository.findByBookId("book1")).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews/book/book1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("john"));

        verify(reviewRepository, times(1)).findByBookId("book1");
    }

    @WithMockUser
    public void testGetReviewsByBook_EmptyList() throws Exception {
        when(reviewRepository.findByBookId("book999")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/reviews/book/book999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @WithMockUser
    public void testAddReview() throws Exception {
        when(reviewRepository.save(any(Review.class))).thenReturn(review1);

        mockMvc.perform(post("/api/reviews")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.rating").value(5));

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @WithMockUser(roles = {"ADMIN"})
    public void testAddReview_AsAdmin() throws Exception {
        when(reviewRepository.save(any(Review.class))).thenReturn(review2);

        mockMvc.perform(post("/api/reviews")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jane"));
    }

    @WithMockUser
    public void testGetReviewsByBook_SingleReview() throws Exception {
        when(reviewRepository.findByBookId("book2")).thenReturn(Arrays.asList(review1));

        mockMvc.perform(get("/api/reviews/book/book2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].comment").value("Excellent book!"));
    }
}
