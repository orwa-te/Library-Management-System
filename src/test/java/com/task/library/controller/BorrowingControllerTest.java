package com.task.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.library.config.SecurityConfig;
import com.task.library.entity.Book;
import com.task.library.entity.BorrowingRecord;
import com.task.library.entity.Patron;
import com.task.library.exception.ResourceNotFoundException;
import com.task.library.service.BorrowingService;
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

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for BorrowingController
 */
@WebMvcTest(BorrowingController.class)
@ExtendWith(SpringExtension.class)
@Import(SecurityConfig.class) // Import security configuration
public class BorrowingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BorrowingService borrowingService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test for POST /api/borrow/{bookId}/patron/{patronId}
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testBorrowBook() throws Exception {
        Long bookId = 1L;
        Long patronId = 1L;

        Book book = new Book();
        book.setId(bookId);

        Patron patron = new Patron();
        patron.setId(patronId);

        BorrowingRecord borrowingRecord = new BorrowingRecord();
        borrowingRecord.setId(1L);
        borrowingRecord.setBook(book);
        borrowingRecord.setPatron(patron);
        borrowingRecord.setBorrowDate(LocalDate.now());

        when(borrowingService.borrowBook(bookId, patronId)).thenReturn(borrowingRecord);

        mockMvc.perform(post("/api/borrow/{bookId}/patron/{patronId}", bookId, patronId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(borrowingRecord.getId()))
                .andExpect(jsonPath("$.borrowDate").value(borrowingRecord.getBorrowDate().toString()));

        verify(borrowingService, times(1)).borrowBook(bookId, patronId);
    }

    /**
     * Test for PUT /api/return/{bookId}/patron/{patronId}
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testReturnBook() throws Exception {
        Long bookId = 1L;
        Long patronId = 1L;

        Book book = new Book();
        book.setId(bookId);

        Patron patron = new Patron();
        patron.setId(patronId);

        BorrowingRecord borrowingRecord = new BorrowingRecord();
        borrowingRecord.setId(1L);
        borrowingRecord.setBook(book);
        borrowingRecord.setPatron(patron);
        borrowingRecord.setBorrowDate(LocalDate.of(2021, 10, 1));
        borrowingRecord.setReturnDate(LocalDate.now());

        when(borrowingService.returnBook(bookId, patronId)).thenReturn(borrowingRecord);

        mockMvc.perform(put("/api/return/{bookId}/patron/{patronId}", bookId, patronId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(borrowingRecord.getId()))
                .andExpect(jsonPath("$.returnDate").value(borrowingRecord.getReturnDate().toString()));

        verify(borrowingService, times(1)).returnBook(bookId, patronId);
    }

    /**
     * Test for POST /api/borrow/{bookId}/patron/{patronId} - Book Not Found
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testBorrowBook_BookNotFound() throws Exception {
        Long bookId = 1L;
        Long patronId = 1L;

        when(borrowingService.borrowBook(bookId, patronId))
                .thenThrow(new ResourceNotFoundException("Book not found with id " + bookId));

        mockMvc.perform(post("/api/borrow/{bookId}/patron/{patronId}", bookId, patronId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found with id " + bookId));

        verify(borrowingService, times(1)).borrowBook(bookId, patronId);
    }
}
