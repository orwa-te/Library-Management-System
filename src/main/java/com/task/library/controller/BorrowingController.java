package com.task.library.controller;

import com.task.library.entity.BorrowingRecord;
import com.task.library.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BorrowingController {

    @Autowired
    private BorrowingService borrowingService;

    @GetMapping("/borrow")
    public ResponseEntity<List<BorrowingRecord>> getBorrows() {
        return ResponseEntity.ok(borrowingService.getBorrows());
    }

    @PostMapping("/borrow/{bookId}/patron/{patronId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowingRecord borrowBook(@PathVariable Long bookId, @PathVariable Long patronId) {
        return borrowingService.borrowBook(bookId, patronId);
    }

    @PutMapping("/return/{bookId}/patron/{patronId}")
    public ResponseEntity<BorrowingRecord> returnBook(@PathVariable Long bookId, @PathVariable Long patronId) {
        BorrowingRecord record = borrowingService.returnBook(bookId, patronId);
        return ResponseEntity.ok(record);
    }
}
