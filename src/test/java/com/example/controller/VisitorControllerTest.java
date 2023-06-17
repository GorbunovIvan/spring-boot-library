package com.example.controller;

import com.example.exception.VisitorNotFoundException;
import com.example.model.Visitor;
import com.example.service.VisitorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VisitorControllerTest {

    @Autowired
    private MockMvc mvc;
    
    @MockBean
    private VisitorService visitorService;
    
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

        Mockito.reset(visitorService);

        when(visitorService.findAll()).thenReturn(new ArrayList<>(visitors));
        when(visitorService.findById(-1L)).thenThrow(VisitorNotFoundException.class);
        when(visitorService.findByName("none")).thenThrow(VisitorNotFoundException.class);
        when(visitorService.create(newVisitor)).thenReturn(newVisitor);
        when(visitorService.update(-1L, newVisitor)).thenThrow(VisitorNotFoundException.class);
        doNothing().when(visitorService).deleteById(anyLong());

        for (var visitor : visitors) {
            when(visitorService.findById(visitor.getId())).thenReturn(visitor);
            when(visitorService.findByName(visitor.getName())).thenReturn(visitor);
            when(visitorService.create(visitor)).thenReturn(visitor);
            when(visitorService.update(visitor.getId(), visitor)).thenReturn(visitor);
        }
    }

    @Test
    void testFindById() throws Exception {

        var visitor = visitors.iterator().next();

        mvc.perform(get("/visitors/{id}", visitor.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("visitors/visitor"))
                .andExpect(content().string(containsString(visitor.getName())))
                .andExpect(content().string(containsString("Edit")));

        verify(visitorService, times(1)).findById(visitor.getId());
    }

    @Test
    void testFindAll() throws Exception {

       var result = mvc.perform(get("/visitors"))
                        .andExpect(status().isOk())
                        .andExpect(view().name("visitors/visitors"));

       for (var visitor : visitors) {
           result.andExpect(content().string(containsString(visitor.getName())));
       }

       verify(visitorService, times(1)).findAll();
    }

    @Test
    void testCreateForm() throws Exception {
        mvc.perform(get("/visitors/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("visitors/newForm"))
                .andExpect(content().string(containsString("Add")));
    }

    @Test
    void testCreate() throws Exception {

        mvc.perform(post("/visitors")
                        .param("name", newVisitor.getName()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/visitors"));

        verify(visitorService, times(1)).create(any(Visitor.class));
    }

    @Test
    void testEditForm() throws Exception {

        var visitor = visitors.iterator().next();

        mvc.perform(get("/visitors/{id}/edit", visitor.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("visitors/editForm"))
                .andExpect(content().string(containsString("Update")));

        verify(visitorService, times(1)).findById(visitor.getId());
    }

    @Test
    void testUpdate() throws Exception {

        var visitor = visitors.iterator().next();

        mvc.perform(put("/visitors/{id}", visitor.getId())
                        .param("name", visitor.getName()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/visitors/" + visitor.getId()));

        verify(visitorService, times(1)).update(anyLong(), any(Visitor.class));
    }

    @Test
    void testDeleteById() throws Exception {

        var visitor = visitors.iterator().next();

        mvc.perform(delete("/visitors/{id}", visitor.getId()))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/visitors"));

        verify(visitorService, times(1)).deleteById(anyLong());
    }
}