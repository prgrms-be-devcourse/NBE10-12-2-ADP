package com.back.domain.book.repository;

import com.back.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Review, Long> {
}
