package com.back.domain.book.controller;

import com.back.domain.book.dto.BookDto;
import com.back.domain.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class ApiV1BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookDto> getBooks() {
        return bookService.getBooks();
    }
}
