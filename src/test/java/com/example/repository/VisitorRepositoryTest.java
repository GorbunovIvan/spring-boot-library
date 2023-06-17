package com.example.repository;

import com.example.model.Visitor;
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
class VisitorRepositoryTest {

    @Autowired
    private VisitorRepository visitorRepository;

    private Set<Visitor> visitors;

    @BeforeEach
    void setUp() {
        visitors = Set.of(
                visitorRepository.save(Visitor.builder().name("first test").build()),
                visitorRepository.save(Visitor.builder().name("second test").build()),
                visitorRepository.save(Visitor.builder().name("third test").build())
        );
    }

    @Test
    void testFindByName() {

        for (var visitor : visitors) {
            Optional<Visitor> visitorFound = visitorRepository.findByName(visitor.getName());
            assertTrue(visitorFound.isPresent());
            assertEquals(visitor, visitorFound.get());
        }

        Optional<Visitor> visitorFound = visitorRepository.findByName("none");
        assertTrue(visitorFound.isEmpty());
    }
}