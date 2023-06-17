package com.example.service;

import com.example.exception.BorrowingRecordNotFoundException;
import com.example.model.Author;
import com.example.model.Book;
import com.example.model.BorrowingRecord;
import com.example.model.Visitor;
import com.example.repository.BorrowingRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class BorrowingRecordServiceTest {

    @InjectMocks
    private BorrowingRecordService recordService;

    @Mock
    private BorrowingRecordRepository recordRepository;

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

        Mockito.reset(recordRepository);

        when(recordRepository.findAllEagerly()).thenReturn(records);
        when(recordRepository.findAllByBook(null)).thenReturn(Collections.emptySet());
        when(recordRepository.findAllByVisitor(null)).thenReturn(Collections.emptySet());
        when(recordRepository.findAllActive()).thenReturn(records.stream().filter(BorrowingRecord::isActive).collect(Collectors.toSet()));
        when(recordRepository.findById(-1L)).thenReturn(Optional.empty());
        when(recordRepository.existsById(-1L)).thenReturn(false);
        when(recordRepository.save(newRecord)).thenReturn(newRecord);
        when(recordRepository.saveWithDetached(newRecord)).thenReturn(newRecord);
        doNothing().when(recordRepository).deleteById(anyLong());

        for (var record : records) {
            when(recordRepository.findById(record.getId())).thenReturn(Optional.of(record));
            when(recordRepository.existsById(record.getId())).thenReturn(true);
            when(recordRepository.save(record)).thenReturn(record);
            when(recordRepository.saveWithDetached(record)).thenReturn(record);
        }

        for (var book : books) {
            when(recordRepository.findAllByBook(book)).thenReturn(book.getBorrowingRecords());
        }

        for (var visitor : visitors) {
            when(recordRepository.findAllByVisitor(visitor)).thenReturn(visitor.getBorrowingRecords());
            when(recordRepository.findAllActiveByVisitor(visitor)).thenReturn(visitor.getBorrowingRecords().stream().filter(BorrowingRecord::isActive).collect(Collectors.toSet()));
        }
    }

    @Test
    void testFindById() {

        for (var record : records) {
            assertEquals(record, recordService.findById(record.getId()));
            verify(recordRepository, times(1)).findById(record.getId());
        }

        assertThrows(BorrowingRecordNotFoundException.class, () -> recordService.findById(-1L));
        verify(recordRepository, times(1)).findById(-1L);
    }

    @Test
    void testFindAll() {
        assertEquals(records, recordService.findAll());
        verify(recordRepository, times(1)).findAllEagerly();
    }

    @Test
    void testFindAllByBook() {

        for (var book : books) {
            assertEquals(book.getBorrowingRecords(), recordService.findAllByBook(book));
            verify(recordRepository, times(1)).findAllByBook(book);
        }

        assertTrue(recordService.findAllByBook(null).isEmpty());
        verify(recordRepository, times(1)).findAllByBook(null);
    }

    @Test
    void testFindAllByVisitor() {

        for (var visitor : visitors) {
            assertEquals(visitor.getBorrowingRecords(), recordService.findAllByVisitor(visitor));
            verify(recordRepository, times(1)).findAllByVisitor(visitor);
        }

        assertTrue(recordService.findAllByVisitor(null).isEmpty());
        verify(recordRepository, times(1)).findAllByVisitor(null);
    }

    @Test
    void testFindAllActive() {
        var recordsExpected = records.stream().filter(BorrowingRecord::isActive).collect(Collectors.toSet());
        assertEquals(recordsExpected, recordService.findAllActive());
        verify(recordRepository, times(1)).findAllActive();
    }

    @Test
    void testFindAllActiveByVisitor() {

        for (var visitor : visitors) {
            var recordsExpected = visitor.getBorrowingRecords().stream().filter(BorrowingRecord::isActive).collect(Collectors.toSet());
            assertEquals(recordsExpected, recordService.findAllActiveByVisitor(visitor));
            verify(recordRepository, times(1)).findAllActiveByVisitor(visitor);
        }

        assertTrue(recordService.findAllActiveByVisitor(null).isEmpty());
        verify(recordRepository, times(1)).findAllActiveByVisitor(null);
    }

    @Test
    void testCreate() {
        assertEquals(newRecord, recordService.create(newRecord));
        verify(recordRepository, times(1)).saveWithDetached(any(BorrowingRecord.class));
    }

    @Test
    void testUpdate() {

        for (var record : records) {
            assertEquals(record, recordService.update(record.getId(), record));
            verify(recordRepository, times(1)).existsById(record.getId());
            verify(recordRepository, times(1)).save(record);
        }

        assertThrows(BorrowingRecordNotFoundException.class, () -> recordService.update(-1L, newRecord));
        verify(recordRepository, times(1)).existsById(-1L);
        verify(recordRepository, never()).save(newRecord);
    }

    @Test
    void testDeleteById() {

        for (var record : records) {
            recordService.deleteById(record.getId());
            verify(recordRepository, times(1)).deleteById(record.getId());
        }

        recordService.deleteById(-1L);
        verify(recordRepository, times(1)).deleteById(-1L);
    }
}