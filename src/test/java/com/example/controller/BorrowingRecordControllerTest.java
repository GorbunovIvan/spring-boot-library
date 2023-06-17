package com.example.controller;

import com.example.exception.BookNotFoundException;
import com.example.exception.BorrowingRecordNotFoundException;
import com.example.exception.VisitorNotFoundException;
import com.example.model.Author;
import com.example.model.Book;
import com.example.model.BorrowingRecord;
import com.example.model.Visitor;
import com.example.service.BookService;
import com.example.service.BorrowingRecordService;
import com.example.service.VisitorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BorrowingRecordControllerTest {
    
    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private BorrowingRecordService recordService;
    @MockBean
    private BookService bookService;
    @MockBean
    private VisitorService visitorService;

    private Set<BorrowingRecord> records;
    private List<Book> books;
    private List<Visitor> visitors;

    private BorrowingRecord newRecord;

    @BeforeEach
    void setUp() {

        var author1 = Author.builder().id(1L).name("first author").build();
        var author2 = Author.builder().id(2L).name("second author").build();

        books = List.of(
                Book.builder().id(1L).name("first book").author(author2).year(1923).build(),
                Book.builder().id(2L).name("second book").author(author1).year(2001).build(),
                Book.builder().id(3L).name("third book").author(author1).year(1975).build()
        );

        visitors = List.of(
                Visitor.builder().id(1L).name("first visitor").build(),
                Visitor.builder().id(2L).name("second visitor").build(),
                Visitor.builder().id(3L).name("third visitor").build()
        );

        records = Set.of(
                BorrowingRecord.builder().id(1L).book(books.get(2)).visitor(visitors.get(0)).dayOfBorrowing(LocalDate.of(2023, Month.DECEMBER, 23)).dayOfReturning(null).build(),
                BorrowingRecord.builder().id(2L).book(books.get(0)).visitor(visitors.get(2)).dayOfBorrowing(LocalDate.of(2022, Month.JULY, 3)).dayOfReturning(LocalDate.of(2023, Month.JULY, 21)).build(),
                BorrowingRecord.builder().id(3L).book(books.get(1)).visitor(visitors.get(1)).dayOfBorrowing(LocalDate.of(2023, Month.APRIL, 15)).dayOfReturning(LocalDate.of(2023, Month.MAY, 11)).build()
        );

        newRecord = BorrowingRecord.builder().id(4L).book(books.get(2)).visitor(visitors.get(1)).dayOfBorrowing(LocalDate.of(2023, Month.APRIL, 15)).dayOfReturning(null).build();

        for (var visitor : visitors) {
            visitor.setBorrowingRecords(records.stream()
                    .filter(r -> r.getVisitor().equals(visitor))
                    .collect(Collectors.toSet()));
        }

        for (var book : books) {
            book.setBorrowingRecords(records.stream()
                    .filter(r -> r.getBook().equals(book))
                    .collect(Collectors.toSet()));
        }

        Mockito.reset(recordService, bookService, visitorService);

        // records
        when(recordService.findAll()).thenReturn(records);
        when(recordService.findAllByBook(null)).thenReturn(Collections.emptySet());
        when(recordService.findAllByVisitor(null)).thenReturn(Collections.emptySet());
        when(recordService.findAllActive()).thenReturn(records.stream().filter(BorrowingRecord::isActive).collect(Collectors.toSet()));
        when(recordService.findById(-1L)).thenThrow(BorrowingRecordNotFoundException.class);
        when(recordService.create(newRecord)).thenReturn(newRecord);
        when(recordService.update(-1L, newRecord)).thenThrow(BorrowingRecordNotFoundException.class);
        doNothing().when(recordService).deleteById(anyLong());

        for (var record : records) {
            when(recordService.findById(record.getId())).thenReturn(record);
            when(recordService.create(record)).thenReturn(record);
            when(recordService.update(record.getId(), record)).thenReturn(record);
        }

        for (var book : books) {
            when(recordService.findAllByBook(book)).thenReturn(book.getBorrowingRecords());
        }

        for (var visitor : visitors) {
            when(recordService.findAllByVisitor(visitor)).thenReturn(visitor.getBorrowingRecords());
            when(recordService.findAllActiveByVisitor(visitor)).thenReturn(visitor.getBorrowingRecords().stream().filter(BorrowingRecord::isActive).collect(Collectors.toSet()));
        }

        // books
        when(bookService.findById(-1L)).thenThrow(BookNotFoundException.class);
        when(bookService.findByName("none")).thenThrow(BookNotFoundException.class);

        for (var book : books) {
            when(bookService.findById(book.getId())).thenReturn(book);
            when(bookService.findByName(book.getName())).thenReturn(book);
        }

        // visitors
        when(visitorService.findById(-1L)).thenThrow(VisitorNotFoundException.class);
        when(visitorService.findByName("none")).thenThrow(VisitorNotFoundException.class);

        for (var visitor : visitors) {
            when(visitorService.findById(visitor.getId())).thenReturn(visitor);
            when(visitorService.findByName(visitor.getName())).thenReturn(visitor);
        }
    }

    @Test
    void testFindById() throws Exception {

        var record = records.iterator().next();

        mvc.perform(get("/records/{id}", record.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("records/record"))
                .andExpect(content().string(containsString(record.getBook().getName())))
                .andExpect(content().string(containsString(record.getVisitor().getName())))
                .andExpect(content().string(containsString("Edit")))
                .andExpect(content().string(containsString("Delete")));

        verify(recordService, times(1)).findById(record.getId());
    }

    @Test
    void testFindAll() throws Exception {

        var result = mvc.perform(get("/records"))
                        .andExpect(status().isOk())
                        .andExpect(view().name("records/records"));

        for (var record : records) {
            result.andExpect(content().string(containsString(record.getBook().getName())));
            result.andExpect(content().string(containsString(record.getVisitor().getName())));
        }

        verify(recordService, times(1)).findAll();
    }

    @Test
    void testFindAllByBook() throws Exception {

        var book = books.iterator().next();

        String html = mvc.perform(get("/records/book/{bookId}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("records/records"))
                .andExpect(content().string(containsString("All records by book")))
                .andExpect(content().string(containsString(book.getName())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var otherBooks = books.stream().filter(b -> !b.equals(book)).toList();

        for (var otherBook : otherBooks) {
            assertFalse(html.contains(otherBook.getName()));
        }

        verify(recordService, times(1)).findAllByBook(book);
    }

    @Test
    void testFindAllByVisitor() throws Exception {

        var visitor = visitors.iterator().next();

        String html = mvc.perform(get("/records/visitor/{bookId}", visitor.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("records/records"))
                .andExpect(content().string(containsString("All records by visitor")))
                .andExpect(content().string(containsString(visitor.getName())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var booksByVisitor = records.stream().filter(r -> r.getVisitor().equals(visitor)).map(BorrowingRecord::getBook).toList();
        for (var book : booksByVisitor) {
            assertTrue(html.contains(book.getName()));
        }

        var otherVisitors = visitors.stream().filter(b -> !b.equals(visitor)).toList();
        for (var otherVisitor : otherVisitors) {
            assertFalse(html.contains(otherVisitor.getName()));
        }

        verify(recordService, times(1)).findAllByVisitor(visitor);
    }

    @Test
    void testFindAllActive() throws Exception {

        var result = mvc.perform(get("/records/active"))
                .andExpect(status().isOk())
                .andExpect(view().name("records/records"))
                .andExpect(content().string(containsString("All active records")));

        var recordsExpected = records.stream().filter(BorrowingRecord::isActive).toList();

        for (var record : recordsExpected) {
            result.andExpect(content().string(containsString(record.getBook().getName())));
            result.andExpect(content().string(containsString(record.getVisitor().getName())));
        }

        verify(recordService, times(1)).findAllActive();
    }

    @Test
    void testFindAllActiveByVisitor() throws Exception {

        var visitor = visitors.iterator().next();

        String html = mvc.perform(get("/records/active/visitor/{visitor}", visitor.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("records/records"))
                .andExpect(content().string(containsString("All active records by visitor")))
                .andExpect(content().string(containsString(visitor.getName())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var booksByVisitor = records.stream().filter(r -> r.getVisitor().equals(visitor)).filter(BorrowingRecord::isActive).map(BorrowingRecord::getBook).toList();
        for (var book : booksByVisitor) {
            assertTrue(html.contains(book.getName()));
        }

        var otherVisitors = visitors.stream().filter(b -> !b.equals(visitor)).toList();
        for (var otherVisitor : otherVisitors) {
            assertFalse(html.contains(otherVisitor.getName()));
        }

        verify(recordService, times(1)).findAllActiveByVisitor(visitor);
    }

    @Test
    void testCreateForm() throws Exception {
        mvc.perform(get("/records/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("records/newForm"))
                .andExpect(content().string(containsString("Add")));
    }

    @Test
    void testCreate() throws Exception {

        mvc.perform(post("/records")
                        .param("book", newRecord.getBook().getName())
                        .param("visitor", newRecord.getVisitor().getName()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/records"));

        verify(bookService, times(1)).findByName(newRecord.getBook().getName());
        verify(visitorService, times(1)).findByName(newRecord.getVisitor().getName());
        verify(recordService, times(1)).create(any(BorrowingRecord.class));
    }

    @Test
    void testEditForm() throws Exception {

        var record = records.iterator().next();

        mvc.perform(get("/records/{id}/edit", record.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("records/editForm"))
                .andExpect(content().string(containsString(record.getBook().getName())))
                .andExpect(content().string(containsString(record.getVisitor().getName())))
                .andExpect(content().string(containsString("Update")));

        verify(recordService, times(1)).findById(record.getId());
    }

    @Test
    void testUpdate() throws Exception {

        var record = records.iterator().next();

        mvc.perform(put("/records/{id}", record.getId())
                        .param("book", record.getBook().getName())
                        .param("visitor", record.getVisitor().getName()))
//                        .param("dayOfBorrowing", String.valueOf(newRecord.getDayOfBorrowing()))
//                        .param("dayOfReturning", String.valueOf(newRecord.getDayOfReturning())))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/records/" + record.getId()));

        verify(recordService, times(1)).findById(record.getId());
        verify(bookService, never()).findByName(record.getBook().getName());
        verify(visitorService, never()).findByName(record.getVisitor().getName());
        verify(recordService, times(1)).update(anyLong(), any(BorrowingRecord.class));
    }

    @Test
    void testDeleteById() throws Exception {

        var record = records.iterator().next();

        mvc.perform(delete("/records/{id}", record.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/records"));

        verify(recordService, times(1)).deleteById(record.getId());
    }
}