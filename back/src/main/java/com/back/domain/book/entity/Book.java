package com.back.domain.book.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Book extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(unique = true)
    private String isbn;

    private String authors;

    private LocalDateTime publishedDate;

    private String publisher;

    private String imgUrl;

    public Book(String title, String description, String isbn,
                String authors, LocalDateTime publishedDate,
                String publisher, String imgUrl) {
        this.title = title;
        this.description = description;
        this.isbn = isbn;
        this.authors = authors;
        this.publishedDate = publishedDate;
        this.publisher = publisher;
        this.imgUrl = imgUrl;
    }
}
