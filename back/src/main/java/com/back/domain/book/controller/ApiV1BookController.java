package com.back.domain.book.controller;

import com.back.domain.book.dto.BookDto;
import com.back.domain.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "ApiV1BookController", description = "API 도서 컨트롤러")
public class ApiV1BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "도서 다건 조회")
    public List<BookDto> getBooks() {
        return bookService.getBooks();
    }
}
