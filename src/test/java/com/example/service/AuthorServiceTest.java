package com.example.service;

import com.example.exception.AuthorNotFoundException;
import com.example.model.Author;
import com.example.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthorServiceTest {

    @InjectMocks
    private AuthorService authorService;

    @Mock
    private AuthorRepository authorRepository;

    private Set<Author> authors;

    private Author newAuthor;

    @BeforeEach
    void setUp() {

        authors = Set.of(
                Author.builder().id(1L).name("first test author").build(),
                Author.builder().id(2L).name("second test author").build(),
                Author.builder().id(3L).name("third test author").build()
        );

        newAuthor = Author.builder().id(4L).name("new test author").build();

        Mockito.reset(authorRepository);

        when(authorRepository.findAll()).thenReturn(new ArrayList<>(authors));
        when(authorRepository.findById(-1L)).thenReturn(Optional.empty());
        when(authorRepository.findByName("none")).thenReturn(Optional.empty());
        when(authorRepository.existsById(-1L)).thenReturn(false);
        when(authorRepository.existsByName("none")).thenReturn(false);
        when(authorRepository.save(newAuthor)).thenReturn(newAuthor);
        doNothing().when(authorRepository).deleteById(anyLong());

        for (var author : authors) {
            when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));
            when(authorRepository.findByName(author.getName())).thenReturn(Optional.of(author));
            when(authorRepository.existsById(author.getId())).thenReturn(true);
            when(authorRepository.existsByName(author.getName())).thenReturn(true);
            when(authorRepository.save(author)).thenReturn(author);
        }
    }

    @Test
    void testFindById() {

        for (var author : authors) {
            assertEquals(author, authorService.findById(author.getId()));
            verify(authorRepository, times(1)).findById(author.getId());
        }

        assertThrows(AuthorNotFoundException.class, () -> authorService.findById(-1L));
        verify(authorRepository, times(1)).findById(-1L);
    }

    @Test
    void testFindByName() {

        for (var author : authors) {
            assertEquals(author, authorService.findByName(author.getName()));
            verify(authorRepository, times(1)).findByName(author.getName());
        }

        assertThrows(AuthorNotFoundException.class, () -> authorService.findByName("none"));
        verify(authorRepository, times(1)).findByName("none");
    }

    @Test
    void testExistsByName() {

        for (var author : authors) {
            assertTrue(authorService.existsByName(author.getName()));
            verify(authorRepository, times(1)).existsByName(author.getName());
        }

        assertFalse(authorService.existsByName("none"));
        verify(authorRepository, times(1)).existsByName("none");
    }

    @Test
    void testFindAll() {
        assertEquals(authors, new HashSet<>(authorService.findAll()));
        verify(authorRepository, times(1)).findAll();
    }

    @Test
    void testCreate() {
        authorService.create(newAuthor);
        verify(authorRepository, times(1)).save(newAuthor);
    }

    @Test
    void testUpdate() {

        for (var author : authors) {
            assertEquals(author, authorService.update(author.getId(), author));
            verify(authorRepository, times(1)).save(author);
        }

        assertThrows(AuthorNotFoundException.class, () -> authorService.update(-1L, newAuthor));
        verify(authorRepository, times(1)).existsById(-1L);
        verify(authorRepository, never()).save(newAuthor);
    }

    @Test
    void testDeleteById() {
        var author = authors.iterator().next();
        authorService.deleteById(author.getId());
        verify(authorRepository, times(1)).deleteById(author.getId());
    }
}