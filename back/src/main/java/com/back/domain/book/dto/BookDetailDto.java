package com.back.domain.book.dto;

import com.back.domain.book.entity.Book;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookDetailDto(
        Long id,
        String title,
        String description,
        String isbn,
        String publishedDate,
        List<String> authors,
        String publisher,
        List<String> translators,
        String imgUrl,
        int reviewCount,
        Map<String, Object> rating,
        List<String> tags,
        Boolean isWished
) {
    public BookDetailDto(Book book, Boolean isWished, Map<String, Object> ratingMap, List<String> tags) {
        this(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getIsbn(),
                formatPublishedDate(book),
                parseAuthors(book),
                book.getPublisher(),
                List.of(),
                book.getImgUrl(),
                book.getReviewCount(),
                ratingMap,
                tags,
                isWished
        );
    }

    private static String formatPublishedDate(Book book) {
        if (book.getPublishedDate() == null) return "";
        return book.getPublishedDate().toLocalDate().toString();
    }

    private static List<String> parseAuthors(Book book) {
        if (book.getAuthors() == null || book.getAuthors().isBlank()) return List.of();
        return List.of(book.getAuthors().split(",\\s*"));
    }
}