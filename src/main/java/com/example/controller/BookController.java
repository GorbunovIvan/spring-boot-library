package com.example.controller;

import com.example.model.Author;
import com.example.model.Book;
import com.example.service.AuthorService;
import com.example.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        return "books/book";
    }

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/books";
    }
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("book", Book.builder().build());
        return "books/newForm";
    }

    @PostMapping
    public String create(@RequestParam("name") String name,
                         @RequestParam("author") String authorName,
                         @RequestParam("year") Integer year) {

        Author author;
        
        if (authorService.existsByName(authorName)) {
            author = authorService.findByName(authorName);
        } else {
            author = Author.builder().name(authorName).build();
        }

        var book = Book.builder()
                .name(name)
                .author(author)
                .year(year)
                .build();

        bookService.create(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var book = bookService.findById(id);
        model.addAttribute("book", book);
        return "books/editForm";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam("name") String name,
                         @RequestParam("author") String authorName,
                         @RequestParam("year") Integer year) {

        var book = bookService.findById(id);

        book.setName(name);

        if (!book.getAuthor().getName().equals(authorName)) {
            Author author;
            if (authorService.existsByName(authorName)) {
                author = authorService.findByName(authorName);
            } else {
                author = Author.builder().name(authorName).build();
            }
            book.setAuthor(author);
        }

        book.setYear(year);

        bookService.update(id, book);
        return "redirect:/books/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }
}
