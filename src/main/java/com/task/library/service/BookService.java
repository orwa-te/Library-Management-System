package com.task.library.service;

import com.task.library.entity.Book;
import com.task.library.exception.ResourceNotFoundException;
import com.task.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Cacheable("books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Cacheable(value = "books", key = "#id")
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
    }

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public Book updateBook(Long id, Book bookDetails) {
        Book book = getBookById(id);

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setIsbn(bookDetails.getIsbn());

        return bookRepository.save(book);
    }

    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        bookRepository.delete(book);
    }
}
