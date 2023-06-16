package com.example.repository;

import com.example.model.Author;
import com.example.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQLDB)
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Set<Book> books;

    @BeforeEach
    void setUp() {

        var author1 = Author.builder().name("first author").build();
        var author2 = Author.builder().name("second author").build();

        books = Set.of(
                bookRepository.save(Book.builder().name("first test").author(author2).year(1923).build()),
                bookRepository.save(Book.builder().name("second test").author(author1).year(2001).build()),
                bookRepository.save(Book.builder().name("third test").author(author1).year(1975).build())
        );
    }

    @Test
    void testFindAllEagerly() {
        assertEquals(books, bookRepository.findAllEagerly());
    }

    @Test
    void testFindByIdEagerly() {

        for (var book : books) {
            Optional<Book> bookFound = bookRepository.findByIdEagerly(book.getId());
            assertTrue(bookFound.isPresent());
            assertEquals(book, bookFound.get());
        }

        assertEquals(Optional.empty(), bookRepository.findByIdEagerly(-1L));
    }

    @Test
    void testFindByName() {

        for (var book : books) {
            Optional<Book> bookFound = bookRepository.findByName(book.getName());
            assertTrue(bookFound.isPresent());
            assertEquals(book, bookFound.get());
        }

        assertEquals(Optional.empty(), bookRepository.findByName("none"));
    }

    @Test
    void testFindAllByAuthorId() {

        var authors = books.stream().map(Book::getAuthor).distinct().toList();

        for (var author : authors) {

            Set<Book> booksFound = bookRepository.findAllByAuthorId(author.getId());
            Set<Book> booksExpected = books.stream().filter(b -> b.getAuthor().equals(author)).collect(Collectors.toSet());

            assertEquals(booksExpected, booksFound);
        }

        assertTrue(bookRepository.findAllByAuthorId(-1L).isEmpty());
    }
}