package com.back.domain.wish.repository;

import com.back.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Review, Long> {
}
