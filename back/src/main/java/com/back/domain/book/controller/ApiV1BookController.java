package com.back.domain.book.controller;

import com.back.domain.book.dto.BookDetailDto;
import com.back.domain.book.dto.BookDto;
import com.back.domain.book.service.BookService;
import com.back.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class ApiV1BookController {
    private final BookService bookService;
    private final Rq rq;

    @GetMapping
    public List<BookDto> getBooks() {
        return bookService.getBooks();
    }

    @GetMapping("/{id}")
    public BookDetailDto getBook(@PathVariable long id) {
        return bookService.getBook(id, rq.getActor());
    }
}
