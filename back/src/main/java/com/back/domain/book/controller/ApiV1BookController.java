package com.back.domain.book.controller;

import com.back.domain.book.dto.BookDetailDto;
import com.back.domain.book.dto.BookDto;
import com.back.domain.book.service.BookService;
import com.back.global.rq.Rq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "ApiV1BookController", description = "API 도서 컨트롤러")
public class ApiV1BookController {
    private final BookService bookService;
    private final Rq rq;

    @GetMapping
    @Operation(summary = "도서 다건 조회")
    public List<BookDto> getBooks() {
        return bookService.getBooks();
    }

    @GetMapping("/{id}")
    public BookDetailDto getBook(@PathVariable long id) {
        return bookService.getBook(id, rq.getActor());
    }
}
