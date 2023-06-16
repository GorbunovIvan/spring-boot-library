package com.example.repository;

import com.example.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQLDB)
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    private Set<Author> authors;

    @BeforeEach
    void setUp() {
        authors = Set.of(
                authorRepository.save(Author.builder().name("first test author").build()),
                authorRepository.save(Author.builder().name("second test author").build()),
                authorRepository.save(Author.builder().name("third test author").build())
        );
    }

    @Test
    void findByName() {

        for (var author : authors) {
            Optional<Author> authorFound = authorRepository.findByName(author.getName());
            assertTrue(authorFound.isPresent());
            assertEquals(author, authorFound.get());
        }

        assertFalse(authorRepository.findByName("none").isPresent());
    }

    @Test
    void existsByName() {

        for (var author : authors) {
            assertTrue(authorRepository.existsByName(author.getName()));
        }

        assertFalse(authorRepository.existsByName("none"));
    }
}