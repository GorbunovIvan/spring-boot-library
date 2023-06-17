package com.example.service;

import com.example.exception.VisitorNotFoundException;
import com.example.model.Visitor;
import com.example.repository.VisitorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class VisitorServiceTest {

    @InjectMocks
    private VisitorService visitorService;
    
    @Mock
    private VisitorRepository visitorRepository;

    private Set<Visitor> visitors;
    private Visitor newVisitor;

    @BeforeEach
    void setUp() {
        
        visitors = Set.of(
                Visitor.builder().id(1L).name("first test").build(),
                Visitor.builder().id(2L).name("second test").build(),
                Visitor.builder().id(3L).name("third test").build()
        );

        newVisitor = Visitor.builder().id(4L).name("new test").build();

        Mockito.reset(visitorRepository);

        when(visitorRepository.findAll()).thenReturn(new ArrayList<>(visitors));
        when(visitorRepository.findById(-1L)).thenReturn(Optional.empty());
        when(visitorRepository.findByName("none")).thenReturn(Optional.empty());
        when(visitorRepository.existsById(-1L)).thenReturn(false);
        when(visitorRepository.save(newVisitor)).thenReturn(newVisitor);
        doNothing().when(visitorRepository).deleteById(anyLong());

        for (var visitor : visitors) {
            when(visitorRepository.findById(visitor.getId())).thenReturn(Optional.of(visitor));
            when(visitorRepository.findByName(visitor.getName())).thenReturn(Optional.of(visitor));
            when(visitorRepository.existsById(visitor.getId())).thenReturn(true);
            when(visitorRepository.save(visitor)).thenReturn(visitor);
        }
    }

    @Test
    void testFindById() {

        for (var visitor : visitors) {
            assertEquals(visitor, visitorService.findById(visitor.getId()));
            verify(visitorRepository, times(1)).findById(visitor.getId());
        }

        assertThrows(VisitorNotFoundException.class, () -> visitorService.findById(-1L));
        verify(visitorRepository, times(1)).findById(-1L);
    }

    @Test
    void testFindByName() {

        for (var visitor : visitors) {
            assertEquals(visitor, visitorService.findByName(visitor.getName()));
            verify(visitorRepository, times(1)).findByName(visitor.getName());
        }

        assertThrows(VisitorNotFoundException.class, () -> visitorService.findByName("none"));
        verify(visitorRepository, times(1)).findByName("none");
    }

    @Test
    void testFindAll() {
        assertEquals(new ArrayList<>(visitors), visitorService.findAll());
        verify(visitorRepository, times(1)).findAll();
    }

    @Test
    void testCreate() {
        assertEquals(newVisitor, visitorService.create(newVisitor));
        verify(visitorRepository, times(1)).save(newVisitor);
    }

    @Test
    void testUpdate() {

        for (var visitor : visitors) {
            assertEquals(visitor, visitorService.update(visitor.getId(), visitor));
            verify(visitorRepository, times(1)).existsById(visitor.getId());
            verify(visitorRepository, times(1)).save(visitor);
        }

        assertThrows(VisitorNotFoundException.class, () -> visitorService.update(-1L, newVisitor));
        verify(visitorRepository, times(1)).existsById(-1L);
        verify(visitorRepository, never()).save(newVisitor);
    }

    @Test
    void testDeleteById() {

        for (var visitor : visitors) {
            visitorService.deleteById(visitor.getId());
            verify(visitorRepository, times(1)).deleteById(visitor.getId());
        }

        visitorService.deleteById(-1L);
        verify(visitorRepository, times(1)).deleteById(-1L);
    }
}