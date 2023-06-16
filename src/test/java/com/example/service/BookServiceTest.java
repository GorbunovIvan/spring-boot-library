package com.example.service;

import com.example.exception.BookNotFoundException;
import com.example.model.Author;
import com.example.model.Book;
import com.example.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    private Set<Book> books;

    private Book newBook;

    @BeforeEach
    void setUp() {

        var author1 = Author.builder().id(1L).name("first author").build();
        var author2 = Author.builder().id(2L).name("second author").build();

        books = Set.of(
                Book.builder().id(1L).name("first test").author(author2).year(1923).build(),
                Book.builder().id(2L).name("second test").author(author1).year(2001).build(),
                Book.builder().id(3L).name("third test").author(author1).year(1975).build()
        );

        newBook = Book.builder().id(4L).name("new test").author(author2).year(2005).build();

        Mockito.reset(bookRepository);

        when(bookRepository.findAllEagerly()).thenReturn(books);
        when(bookRepository.findAllByAuthorId(-1L)).thenReturn(Collections.emptySet());
        when(bookRepository.findById(-1L)).thenReturn(Optional.empty());
        when(bookRepository.findByIdEagerly(-1L)).thenReturn(Optional.empty());
        when(bookRepository.findByName("none")).thenReturn(Optional.empty());
        when(bookRepository.existsById(-1L)).thenReturn(false);
        when(bookRepository.save(newBook)).thenReturn(newBook);
        when(bookRepository.saveWithDetached(newBook)).thenReturn(newBook);
        doNothing().when(bookRepository).deleteById(anyLong());

        for (var book : books) {
            when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
            when(bookRepository.findByIdEagerly(book.getId())).thenReturn(Optional.of(book));
            when(bookRepository.findByName(book.getName())).thenReturn(Optional.of(book));
            when(bookRepository.existsById(book.getId())).thenReturn(true);
            when(bookRepository.save(book)).thenReturn(book);
            when(bookRepository.saveWithDetached(book)).thenReturn(book);
        }

        for (var author : books.stream().map(Book::getAuthor).distinct().toList()) {
            var booksByAuthor = books.stream().filter(b -> b.getAuthor().equals(author)).collect(Collectors.toSet());
            when(bookRepository.findAllByAuthorId(author.getId())).thenReturn(booksByAuthor);
        }
    }

    @Test
    void testFindById() {

        for (var book : books) {
            assertEquals(book, bookService.findById(book.getId()));
            verify(bookRepository, times(1)).findById(book.getId());
        }

        assertThrows(BookNotFoundException.class, () -> bookService.findById(-1L));
        verify(bookRepository, times(1)).findById(-1L);
    }

    @Test
    void testFindByIdEagerly() {

        for (var book : books) {
            assertEquals(book, bookService.findByIdEagerly(book.getId()));
            verify(bookRepository, times(1)).findByIdEagerly(book.getId());
        }

        assertThrows(BookNotFoundException.class, () -> bookService.findByIdEagerly(-1L));
        verify(bookRepository, times(1)).findByIdEagerly(-1L);
    }

    @Test
    void testFindByName() {

        for (var book : books) {
            assertEquals(book, bookService.findByName(book.getName()));
            verify(bookRepository, times(1)).findByName(book.getName());
        }

        assertThrows(BookNotFoundException.class, () -> bookService.findByName("none"));
        verify(bookRepository, times(1)).findByName("none");
    }

    @Test
    void testFindAll() {
        assertEquals(books, bookService.findAll());
        verify(bookRepository, times(1)).findAllEagerly();
    }

    @Test
    void testFindAllByAuthorId() {

        for (var author : books.stream().map(Book::getAuthor).distinct().toList()) {
            var authorsExpected = books.stream().filter(b -> b.getAuthor().equals(author)).collect(Collectors.toSet());
            assertEquals(authorsExpected, bookService.findAllByAuthorId(author.getId()));
            verify(bookRepository, times(1)).findAllByAuthorId(author.getId());
        }

        assertTrue(bookService.findAllByAuthorId(-1L).isEmpty());
        verify(bookRepository, times(1)).findAllByAuthorId(-1L);
    }

    @Test
    void testCreate() {
        assertEquals(newBook, bookService.create(newBook));
        verify(bookRepository, times(1)).saveWithDetached(newBook);
    }

    @Test
    void testUpdate() {

        for (var book : books) {
            assertEquals(book, bookService.update(book.getId(), book));
            verify(bookRepository, times(1)).saveWithDetached(book);
        }

        assertThrows(BookNotFoundException.class, () -> bookService.update(-1L, newBook));
        verify(bookRepository, times(1)).existsById(-1L);
        verify(bookRepository, never()).saveWithDetached(newBook);
    }

    @Test
    void testDeleteById() {

        for (var book : books) {
            bookService.deleteById(book.getId());
            verify(bookRepository, times(1)).deleteById(book.getId());
        }

        bookService.deleteById(-1L);
        verify(bookRepository, times(1)).deleteById(-1L);
    }
}