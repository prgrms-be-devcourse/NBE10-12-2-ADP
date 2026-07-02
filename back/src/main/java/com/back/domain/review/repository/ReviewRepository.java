package com.back.domain.review.repository;

import com.back.domain.book.entity.Book;
import com.back.domain.member.entity.Member;
import com.back.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBook(Book book);

    List<Review> findByReviewer(Member member);

    int countByReviewerAndRating(Member member, float rating);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.reviewer = :member")
    double getAverageRatingByMember(Member member);

    Optional<Review> findFirstByOrderByIdDesc();

    int countByBookAndRating(Book book, float rating);

    Optional<Review> findFirstByBookAndReviewer(Book book, Member reviewer);
}
