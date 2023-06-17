package com.example.repository;

import com.example.model.Author;
import com.example.model.Book;
import com.example.model.BorrowingRecord;
import com.example.model.Visitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.HSQLDB)
class BorrowingRecordRepositoryTest {

    @Autowired
    private BorrowingRecordRepository recordRepository;

    private Set<BorrowingRecord> records;

    @BeforeEach
    void setUp() {

        var author1 = Author.builder().name("first author").build();
        var author2 = Author.builder().name("second author").build();

        var books = List.of(
                Book.builder().name("first test").author(author2).year(1923).build(),
                Book.builder().name("second test").author(author1).year(2001).build(),
                Book.builder().name("third test").author(author1).year(1975).build()
        );

        var visitors = List.of(
                Visitor.builder().name("first test").build(),
                Visitor.builder().name("second test").build(),
                Visitor.builder().name("third test").build()
        );

        records = Set.of(
                recordRepository.save(BorrowingRecord.builder().book(books.get(2)).visitor(visitors.get(0)).dayOfBorrowing(LocalDate.of(2023, Month.DECEMBER, 23)).dayOfReturning(null).build()),
                recordRepository.save(BorrowingRecord.builder().book(books.get(0)).visitor(visitors.get(2)).dayOfBorrowing(LocalDate.of(2022, Month.JULY, 3)).dayOfReturning(LocalDate.of(2023, Month.JULY, 21)).build()),
                recordRepository.save(BorrowingRecord.builder().book(books.get(1)).visitor(visitors.get(1)).dayOfBorrowing(LocalDate.of(2023, Month.APRIL, 15)).dayOfReturning(LocalDate.of(2023, Month.MAY, 11)).build())
        );

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
    }

    @Test
    void testFindAllEagerly() {
        assertEquals(records, recordRepository.findAllEagerly());
    }

    @Test
    void testFindAllByBook() {

        var books = records.stream()
                .map(BorrowingRecord::getBook)
                .distinct()
                .toList();

        for (var book : books) {

            var recordsExpected = records.stream()
                    .filter(r -> r.getBook().equals(book))
                    .collect(Collectors.toSet());

            assertEquals(recordsExpected, recordRepository.findAllByBook(book));
        }
    }

    @Test
    void testFindAllByVisitor() {
        var visitors = records.stream()
                .map(BorrowingRecord::getVisitor)
                .distinct()
                .toList();

        for (var visitor : visitors) {

            var recordsExpected = records.stream()
                    .filter(r -> r.getVisitor().equals(visitor))
                    .collect(Collectors.toSet());

            assertEquals(recordsExpected, recordRepository.findAllByVisitor(visitor));
        }
    }

    @Test
    void testFindAllActive() {

        var recordsExpected = records.stream()
                .filter(BorrowingRecord::isActive)
                .collect(Collectors.toSet());

        assertEquals(recordsExpected, recordRepository.findAllActive());
    }

    @Test
    void testFindAllActiveByVisitor() {

        var visitors = records.stream()
                .filter(BorrowingRecord::isActive)
                .map(BorrowingRecord::getVisitor)
                .distinct()
                .toList();

        for (var visitor : visitors) {

            var recordsExpected = records.stream()
                    .filter(BorrowingRecord::isActive)
                    .filter(r -> r.getVisitor().equals(visitor))
                    .collect(Collectors.toSet());

            assertEquals(recordsExpected, recordRepository.findAllActiveByVisitor(visitor));
        }
    }
}