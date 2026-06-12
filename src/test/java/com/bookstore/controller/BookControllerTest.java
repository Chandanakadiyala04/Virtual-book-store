package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.repository.BookRepository;
import com.bookstore.security.JwtUtils;
import com.bookstore.security.UserDetailsServiceImpl;
import com.bookstore.service.BookImportService;
import com.fasterxml.jackson.databind.ObjectMapper;

// ─── TestNG replaces JUnit 5 ────────────────────────────────────────────────
// REMOVED: import org.junit.jupiter.api.BeforeEach;
// REMOVED: import org.junit.jupiter.api.Test;
import org.testng.annotations.BeforeMethod; // ← replaces @BeforeEach
import org.testng.annotations.Test; // ← replaces JUnit @Test

// Spring test infrastructure (unchanged — Spring Boot test works with TestNG)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests; // ← NEW: needed for Spring + TestNG
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Test(groups = "controller")
public class BookControllerTest extends AbstractTestNGSpringContextTests { // ← KEY CHANGE

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private BookRepository bookRepository;

        @MockBean
        private BookImportService bookImportService;

        @MockBean
        private JwtUtils jwtUtils;

        @MockBean
        private UserDetailsServiceImpl userDetailsService;

        @Autowired
        private ObjectMapper objectMapper;

        private Book book1;
        private Book book2;

        @BeforeMethod
        public void setUp() {
                book1 = new Book("1", "The Great Gatsby", "F. Scott Fitzgerald",
                                "A classic novel", "Fiction", 299.99, 10, "seller1", "image1.jpg");
                book2 = new Book("2", "Clean Code", "Robert C. Martin",
                                "A handbook", "Technology", 499.99, 5, "seller2", "image2.jpg");
        }

        @WithMockUser
        public void testGetAllBooks_ReturnsAllBooks() throws Exception {
                List<Book> books = Arrays.asList(book1, book2);
                when(bookRepository.findAll()).thenReturn(books);

                mockMvc.perform(get("/api/books"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].title").value("The Great Gatsby"));

                verify(bookRepository, times(1)).findAll();
        }

        @WithMockUser
        public void testGetAllBooks_WithTitleFilter() throws Exception {
                when(bookRepository.findByTitleContainingIgnoreCase("gatsby"))
                                .thenReturn(Arrays.asList(book1));

                mockMvc.perform(get("/api/books").param("title", "gatsby"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].title").value("The Great Gatsby"));
        }

        @WithMockUser
        public void testGetAllBooks_EmptyList() throws Exception {
                when(bookRepository.findAll()).thenReturn(Arrays.asList());

                mockMvc.perform(get("/api/books"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        @WithMockUser
        public void testGetBookById_Found() throws Exception {
                when(bookRepository.findById("1")).thenReturn(Optional.of(book1));

                mockMvc.perform(get("/api/books/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("The Great Gatsby"))
                                .andExpect(jsonPath("$.price").value(299.99));
        }

        @WithMockUser
        public void testGetBookById_NotFound() throws Exception {
                when(bookRepository.findById("999")).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/books/999"))
                                .andExpect(status().isNotFound());
        }

        @WithMockUser
        public void testGetBooksByCategory() throws Exception {
                when(bookRepository.findByCategory("Fiction")).thenReturn(Arrays.asList(book1));

                mockMvc.perform(get("/api/books/category/Fiction"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1));
        }

        @WithMockUser
        public void testGetBooksByCategory_NotFound() throws Exception {
                when(bookRepository.findByCategory("Unknown")).thenReturn(Arrays.asList());

                mockMvc.perform(get("/api/books/category/Unknown"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        @WithMockUser
        public void testGetAllCategories() throws Exception {
                mockMvc.perform(get("/api/books/categories"))
                                .andExpect(status().isOk());
        }

        @WithMockUser
        public void testCreateBook() throws Exception {
                when(bookRepository.save(any(Book.class))).thenReturn(book1);

                mockMvc.perform(post("/api/books")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(book1)))
                                .andExpect(status().isOk());
        }

        @WithMockUser(roles = { "ADMIN" })
        public void testUpdateBook_NotFound() throws Exception {
                when(bookRepository.findById("999")).thenReturn(Optional.empty());

                mockMvc.perform(put("/api/books/999")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(book1)))
                                .andExpect(status().isNotFound());
        }

        @WithMockUser(roles = { "ADMIN" })
        public void testDeleteBook_NotFound() throws Exception {
                when(bookRepository.findById("999")).thenReturn(Optional.empty());

                mockMvc.perform(delete("/api/books/999").with(csrf()))
                                .andExpect(status().isNotFound());
        }

        @WithMockUser
        public void testGetBooksBySeller() throws Exception {
                when(bookRepository.findBySellerId("seller1")).thenReturn(Arrays.asList(book1));

                mockMvc.perform(get("/api/books/seller/seller1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1));
        }
}
