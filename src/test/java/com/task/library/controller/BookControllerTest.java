package com.task.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.library.config.SecurityConfig;
import com.task.library.entity.Book;
import com.task.library.exception.ResourceNotFoundException;
import com.task.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for BookController
 */
@WebMvcTest(BookController.class)
@ExtendWith(SpringExtension.class)
@Import(SecurityConfig.class) // Import security configuration
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test for GET /api/books
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetAllBooks() throws Exception {
        Book book1 = new Book(1L, "Book One", "Author One", 2020, "ISBN1", null);
        Book book2 = new Book(2L, "Book Two", "Author Two", 2021, "ISBN2", null);

        when(bookService.getAllBooks()).thenReturn(Arrays.asList(book1, book2));

        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(book1.getId()))
                .andExpect(jsonPath("$[0].title").value(book1.getTitle()))
                .andExpect(jsonPath("$[1].id").value(book2.getId()))
                .andExpect(jsonPath("$[1].title").value(book2.getTitle()));

        verify(bookService, times(1)).getAllBooks();
    }

    /**
     * Test for GET /api/books/{id}
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetBookById() throws Exception {
        Long bookId = 1L;
        Book book = new Book(bookId, "Book One", "Author One", 2020, "ISBN1", null);

        when(bookService.getBookById(bookId)).thenReturn(book);

        mockMvc.perform(get("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()));

        verify(bookService, times(1)).getBookById(bookId);
    }

    /**
     * Test for GET /api/books/{id} - Not Found
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetBookById_NotFound() throws Exception {
        Long bookId = 1L;

        when(bookService.getBookById(bookId))
                .thenThrow(new ResourceNotFoundException("Book not found with id " + bookId));

        mockMvc.perform(get("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found with id " + bookId));

        verify(bookService, times(1)).getBookById(bookId);
    }

    /**
     * Test for POST /api/books
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateBook() throws Exception {
        Book book = new Book(null, "New Book", "New Author", 2021, "ISBN123", null);
        Book savedBook = new Book(1L, "New Book", "New Author", 2021, "ISBN123", null);

        when(bookService.createBook(any(Book.class))).thenReturn(savedBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(savedBook.getId()))
                .andExpect(jsonPath("$.title").value(savedBook.getTitle()));

        verify(bookService, times(1)).createBook(any(Book.class));
    }

    /**
     * Test for PUT /api/books/{id}
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testUpdateBook() throws Exception {
        Long bookId = 1L;
        Book bookDetails = new Book(null, "Updated Title", "Updated Author", 2022, "ISBN999", null);
        Book updatedBook = new Book(bookId, "Updated Title", "Updated Author", 2022, "ISBN999", null);

        when(bookService.updateBook(eq(bookId), any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedBook.getId()))
                .andExpect(jsonPath("$.title").value(updatedBook.getTitle()));

        verify(bookService, times(1)).updateBook(eq(bookId), any(Book.class));
    }

    /**
     * Test for DELETE /api/books/{id}
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeleteBook() throws Exception {
        Long bookId = 1L;

        doNothing().when(bookService).deleteBook(bookId);

        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isOk());

        verify(bookService, times(1)).deleteBook(bookId);
    }

    /**
     * Test for POST /api/books - Validation Error
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testCreateBook_ValidationError() throws Exception {
        Book book = new Book();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("must not be blank"))
                .andExpect(jsonPath("$.author").value("must not be blank"))
                .andExpect(jsonPath("$.publicationYear").value("must not be null"))
                .andExpect(jsonPath("$.isbn").value("must not be blank"));

        verify(bookService, times(0)).createBook(any(Book.class));
    }
}
