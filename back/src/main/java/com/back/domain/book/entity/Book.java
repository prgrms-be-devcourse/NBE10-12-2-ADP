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
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(unique = true)
    private String isbn;

    @Column(columnDefinition = "LONGTEXT")
    private String authors;

    private LocalDateTime publishedDate;

    private String publisher;

    private String imgUrl;

    private double averageRating = 0.0;

    private int reviewCount = 0;

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

    // 새 리뷰 등록 시 평균 별점, 리뷰 수 갱신
    public void addReviewRating(float newRating) {
        double totalRating = (this.averageRating * this.reviewCount) + newRating;
        this.reviewCount++;
        // 반올림
        this.averageRating = Math.round((totalRating / this.reviewCount) * 10.0) / 10.0;
    }

    // 기존 리뷰 삭제되었을 때 평점 갱신
    public void removeReviewRating(float deletedRating) {
        if (this.reviewCount <= 1) {
            this.reviewCount = 0;
            this.averageRating = 0.0;
            return;
        }
        double totalRating = (this.averageRating * this.reviewCount) - deletedRating;
        this.reviewCount--;
        this.averageRating = Math.round((totalRating / this.reviewCount) * 10.0) / 10.0;
    }

    public void modify(String title, String description, String authors, String publisher, String imgUrl) {
        this.title = title;
        this.description = description;
        this.authors = authors;
        this.publisher = publisher;
        this.imgUrl = imgUrl;
    }
}
