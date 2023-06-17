package com.example.controller;

import com.example.model.Visitor;
import com.example.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        model.addAttribute("visitor", visitorService.findById(id));
        return "visitors/visitor";
    }

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("visitors", visitorService.findAll());
        return "visitors/visitors";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("visitor", Visitor.builder().build());
        return "visitors/newForm";
    }

    @PostMapping
    public String create(@ModelAttribute Visitor visitor) {
        visitorService.create(visitor);
        return "redirect:/visitors";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var visitor = visitorService.findById(id);
        model.addAttribute("visitor", visitor);
        return "visitors/editForm";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Visitor visitor) {
        visitorService.update(id, visitor);
        return "redirect:/visitors/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        visitorService.deleteById(id);
        return "redirect:/visitors";
    }
}
