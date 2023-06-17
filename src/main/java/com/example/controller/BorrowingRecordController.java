package com.example.controller;

import com.example.model.BorrowingRecord;
import com.example.service.BookService;
import com.example.service.BorrowingRecordService;
import com.example.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/records")
@RequiredArgsConstructor
public class BorrowingRecordController {

    private final BorrowingRecordService recordService;
    private final BookService bookService;
    private final VisitorService visitorService;

    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        model.addAttribute("record", recordService.findById(id));
        return "records/record";
    }

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("records", recordService.findAll());
        return "records/records";
    }

    @GetMapping("/book/{bookId}")
    public String findAllByBook(@PathVariable Long bookId, Model model) {
        var book = bookService.findById(bookId);
        model.addAttribute("book", book);
        model.addAttribute("records", recordService.findAllByBook(book));
        return "records/records";
    }

    @GetMapping("/visitor/{visitorId}")
    public String findAllByVisitor(@PathVariable Long visitorId, Model model) {
        var visitor = visitorService.findById(visitorId);
        model.addAttribute("visitor", visitor);
        model.addAttribute("records", recordService.findAllByVisitor(visitor));
        return "records/records";
    }

    @GetMapping("/active")
    public String findAllActive(Model model) {
        model.addAttribute("active", true);
        model.addAttribute("records", recordService.findAllActive());
        return "records/records";
    }

    @GetMapping("/active/visitor/{visitorId}")
    public String findAllActiveByVisitor(@PathVariable Long visitorId, Model model) {
        var visitor = visitorService.findById(visitorId);
        model.addAttribute("active", true);
        model.addAttribute("visitor", visitor);
        model.addAttribute("records", recordService.findAllActiveByVisitor(visitor));
        return "records/records";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("record", BorrowingRecord.builder().build());
        return "records/newForm";
    }

    @PostMapping
    public String create(@RequestParam("book") String bookName,
                         @RequestParam("visitor") String visitorName) {

        var book = bookService.findByName(bookName);
        var visitor = visitorService.findByName(visitorName);

        var record = BorrowingRecord.builder()
                        .book(book)
                        .visitor(visitor)
                        .build();

        recordService.create(record);
        return "redirect:/records";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var record = recordService.findById(id);
        model.addAttribute("record", record);
        return "records/editForm";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam("book") String bookName,
                         @RequestParam("visitor") String visitorName,
                         @RequestParam(value = "dayOfBorrowing", required = false) LocalDate dayOfBorrowing,
                         @RequestParam(value = "dayOfReturning", required = false) LocalDate dayOfReturning) {

        var record = recordService.findById(id);

        if (!record.getBook().getName().equals(bookName)) {
            var book = bookService.findByName(bookName);
            record.setBook(book);
        }

        if (!record.getVisitor().getName().equals(visitorName)) {
            var visitor = visitorService.findByName(visitorName);
            record.setVisitor(visitor);
        }

        record.setDayOfBorrowing(dayOfBorrowing);
        record.setDayOfReturning(dayOfReturning);

        recordService.update(id, record);
        return "redirect:/records/" + id;
    }

    @DeleteMapping("/{id}")
    public String deleteById(@PathVariable Long id) {
        recordService.deleteById(id);
        return "redirect:/records";
    }
}