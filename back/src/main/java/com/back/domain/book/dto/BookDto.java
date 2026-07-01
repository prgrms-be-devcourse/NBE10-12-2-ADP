package com.back.domain.book.dto;

import com.back.domain.book.entity.Book;

public record BookDto(
        long id,
        String title,
        String imgUrl,
        double averageRating
) {
    public BookDto(Book book, double averageRating) {
        this(book.getId(), book.getTitle(), book.getImgUrl(), averageRating);
    }
}