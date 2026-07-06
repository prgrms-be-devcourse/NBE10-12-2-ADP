package com.back.domain.book.controller;

import com.back.domain.book.dto.BookDetailDto;
import com.back.domain.book.dto.BookDto;
import com.back.domain.book.service.BookRankService;
import com.back.domain.book.service.BookRecommendService;
import com.back.domain.book.service.BookService;
import com.back.global.rq.Rq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "ApiV1BookController", description = "API 도서 컨트롤러")
public class ApiV1BookController {
    private final BookService bookService;
    private final BookRecommendService bookRecommendService;
    private final Rq rq;
    private final BookRankService bookRankService;

    @GetMapping
    @Operation(summary = "도서 다건 조회")
    public List<BookDto> getBooks() {
        return bookService.getBooks();
    }

    @GetMapping("/{id}")
    @Operation(summary = "도서 단건 조회")
    public BookDetailDto getBook(@PathVariable long id) {
        return bookService.getBook(id, rq.getActor());
    }

    @GetMapping("/search")
    @Operation(summary = "도서 검색")
    public List<BookDto> search(@RequestParam @NotBlank String searchTerm) {
        return bookService.search(searchTerm);
    }

    @GetMapping("/recommend")
    public List<BookDto> recommend() {
        return bookRecommendService.getRecommends(rq.getActor());

    }

    @GetMapping("/rank")
    public List<BookDto> rank(
            @RequestParam(defaultValue = "") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (type.equals("rating")) return bookRankService.getBooksByRating(page, size);
        return bookRankService.getBooksByReviewCnt(page, size);

    }
}
