package com.task.library.service;

import com.task.library.entity.Book;
import com.task.library.entity.BorrowingRecord;
import com.task.library.entity.Patron;
import com.task.library.exception.ResourceNotFoundException;
import com.task.library.repository.BookRepository;
import com.task.library.repository.BorrowingRecordRepository;
import com.task.library.repository.PatronRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowingService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PatronRepository patronRepository;

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    public List<BorrowingRecord> getBorrows(){
        return borrowingRecordRepository.findAll();
    }

    @Transactional
    public BorrowingRecord borrowBook(Long bookId, Long patronId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + bookId));

        Patron patron = patronRepository.findById(patronId)
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with id " + patronId));

        BorrowingRecord borrowingRecord = new BorrowingRecord();
        borrowingRecord.setBook(book);
        borrowingRecord.setPatron(patron);
        borrowingRecord.setBorrowDate(LocalDate.now());

        return borrowingRecordRepository.save(borrowingRecord);
    }

    @Transactional
    public BorrowingRecord returnBook(Long bookId, Long patronId) {
        BorrowingRecord borrowingRecord = borrowingRecordRepository.findByBookIdAndPatronIdAndReturnDateIsNull(bookId, patronId);

        if (borrowingRecord == null) {
            throw new ResourceNotFoundException("Borrowing record not found for book id " + bookId + " and patron id " + patronId);
        }

        borrowingRecord.setReturnDate(LocalDate.now());
        return borrowingRecordRepository.save(borrowingRecord);
    }
}
