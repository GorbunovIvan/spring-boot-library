package com.example.controller;

import com.example.exception.AuthorNotFoundException;
import com.example.exception.BookNotFoundException;
import com.example.model.Author;
import com.example.model.Book;
import com.example.service.AuthorService;
import com.example.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private BookService bookService;
    @MockBean
    private AuthorService authorService;

    private Set<Book> books;
    private Set<Author> authors;

    private Book newBook;

    @BeforeEach
    void setUp() {

        var author1 = Author.builder().id(1L).name("first author").build();
        var author2 = Author.builder().id(2L).name("second author").build();

        authors = Set.of(author1, author2);

        books = Set.of(
                Book.builder().id(1L).name("first test").author(author2).year(1923).borrowingRecords(Collections.emptySet()).build(),
                Book.builder().id(2L).name("second test").author(author1).year(2001).borrowingRecords(Collections.emptySet()).build(),
                Book.builder().id(3L).name("third test").author(author1).year(1975).borrowingRecords(Collections.emptySet()).build()
        );

        for (var author : authors) {
            author.setBooks(books.stream()
                    .filter(b -> b.getAuthor().equals(author))
                    .collect(Collectors.toSet()));
        }

        newBook = Book.builder().id(4L).name("new test").author(author2).year(2005).build();

        Mockito.reset(bookService, authorService);

        // bookService
        when(bookService.findAll()).thenReturn(books);
        when(bookService.findAllByAuthorId(-1L)).thenReturn(Collections.emptySet());
        when(bookService.findById(-1L)).thenThrow(BookNotFoundException.class);
        when(bookService.findByIdEagerly(-1L)).thenThrow(BookNotFoundException.class);
        when(bookService.findByName("none")).thenThrow(BookNotFoundException.class);
        when(bookService.create(newBook)).thenReturn(newBook);
        when(bookService.update(-1L, newBook)).thenReturn(newBook);
        doNothing().when(bookService).deleteById(anyLong());

        for (var book : books) {
            when(bookService.findById(book.getId())).thenReturn(book);
            when(bookService.findByIdEagerly(book.getId())).thenReturn(book);
            when(bookService.findByName(book.getName())).thenReturn(book);
            when(bookService.update(book.getId(), book)).thenReturn(book);
        }

        for (var author : authors) {
            when(bookService.findAllByAuthorId(author.getId())).thenReturn(author.getBooks());
        }

        // authorService
        when(authorService.findAll()).thenReturn(new ArrayList<>(authors));
        when(authorService.findById(-1L)).thenThrow(AuthorNotFoundException.class);
        when(authorService.findByName("none")).thenThrow(AuthorNotFoundException.class);
        when(authorService.existsByName("none")).thenReturn(false);

        for (var author : authors) {
            when(authorService.findById(author.getId())).thenReturn(author);
            when(authorService.findByName(author.getName())).thenReturn(author);
            when(authorService.existsByName(author.getName())).thenReturn(true);
        }
    }

    @Test
    void testFindById() throws Exception {

        var book = books.iterator().next();

        mvc.perform(get("/books/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("books/book"))
                .andExpect(content().string(containsString(book.getName())))
                .andExpect(content().string(containsString(book.getAuthor().getName())))
                .andExpect(content().string(containsString(String.valueOf(book.getYear()))));

        verify(bookService, times(1)).findByIdEagerly(book.getId());
    }

    @Test
    void testFindAll() throws Exception {

        var response = mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/books"));

        for (var book : books) {
            response.andExpect(content().string(containsString(book.getName())));
        }

        verify(bookService, times(1)).findAll();
    }

    @Test
    void testFindAllByAuthorId() throws Exception {

        var author = authors.iterator().next();

        var response = mvc.perform(get("/books/author/{id}", author.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("books/books"));

        for (var book : author.getBooks()) {
            response.andExpect(content().string(containsString(book.getName())));
        }

        verify(bookService, times(1)).findAllByAuthorId(author.getId());
    }

    @Test
    void testCreateForm() throws Exception {
        mvc.perform(get("/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/newForm"))
                .andExpect(content().string(containsString("Add")));
    }

    @Test
    void testCreate() throws Exception {

        mvc.perform(post("/books")
                    .param("name", newBook.getName())
                    .param("author", newBook.getAuthor().getName())
                    .param("year", String.valueOf(newBook.getYear())))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/books"));

        verify(authorService, times(1)).existsByName(newBook.getAuthor().getName());
        verify(authorService, times(1)).findByName(newBook.getAuthor().getName());
        verify(bookService, times(1)).create(any(Book.class));
    }

    @Test
    void testEditForm() throws Exception {

        var book = books.iterator().next();

        mvc.perform(get("/books/{id}/edit", book.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("books/editForm"))
                .andExpect(content().string(containsString("Update")));

        verify(bookService, times(1)).findById(book.getId());
    }

    @Test
    void testUpdate() throws Exception {

        var book = books.iterator().next();

        mvc.perform(put("/books/{id}", book.getId())
                    .param("name", book.getName())
                    .param("author", book.getAuthor().getName())
                    .param("year", String.valueOf(book.getYear())))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/books/" + book.getId()));

        verify(authorService, never()).existsByName(book.getAuthor().getName());
        verify(authorService, never()).findByName(book.getAuthor().getName());
        verify(bookService, times(1)).update(anyLong(), any(Book.class));
    }

    @Test
    void testDeleteById() throws Exception {

        var book = books.iterator().next();

        mvc.perform(delete("/books/{id}", book.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/books"));

        verify(bookService, times(1)).deleteById(book.getId());
    }
}