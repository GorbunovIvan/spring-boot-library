package com.example.service;

import com.example.exception.BookNotFoundException;
import com.example.model.Book;
import com.example.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(BookNotFoundException::new);
    }

    public Book findByIdEagerly(Long id) {
        return bookRepository.findByIdEagerly(id)
                .orElseThrow(BookNotFoundException::new);
    }

    public Book findByName(String name) {
        return bookRepository.findByName(name)
                .orElseThrow(BookNotFoundException::new);
    }

    public Set<Book> findAll() {
        return bookRepository.findAllEagerly();
    }

    public Set<Book> findAllByAuthorId(Long authorId) {
        return bookRepository.findAllByAuthorId(authorId);
    }

    public Book create(Book book) {
        return bookRepository.saveWithDetached(book);
    }

    @Transactional
    public Book update(Long id, Book book) {
        if (!bookRepository.existsById(id))
            throw new BookNotFoundException();
        book.setId(id);
        return bookRepository.saveWithDetached(book);
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
