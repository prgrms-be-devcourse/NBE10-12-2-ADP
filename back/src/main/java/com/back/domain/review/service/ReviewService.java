package com.back.domain.review.service;

import com.back.domain.book.entity.Book;
import com.back.domain.book.repository.BookRepository;
import com.back.domain.member.entity.Member;
import com.back.domain.review.entity.Review;
import com.back.domain.review.repository.ReviewRepository;
import com.back.domain.tag.service.TagService;
import com.back.global.exception.ServiceException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final TagService tagService;

    public List<Review> findByBook(Book book) {
        return reviewRepository.findByBook(book);
    }

    public List<Review> findByMember(Member member) {
        return reviewRepository.findByReviewer(member);
    }

    @Transactional
    public Review addReview(Book book, Member actor, float rating, String comment, List<String> tags) {
        Review review = reviewRepository.save(new Review(book, actor, rating, comment,
                tags.stream().map(tagService::findByNameOrSave).toList()));

        book.addReviewRating(rating);

        return review;
    }


    public Map<String, Object> getRatingMap(Member member) {

        Map<String, Object> ratings = new HashMap<>();

        ratings.put("average", reviewRepository.getAverageRatingByMember(member));

        for (int i = 0; i < 10; i++) {
            float targetRating = (i + 1) * 0.5f;
            ratings.put("%.1f".formatted(targetRating),
                    reviewRepository.countByReviewerAndRating(member, targetRating));
        }

        return ratings;
    }

    public Optional<Review> findLatest() {
        return reviewRepository.findFirstByOrderByIdDesc();
    }

    public Review findById(long id) {
        return reviewRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 리뷰입니다")
        );
    }

    @Transactional
    public void editReview(Review review, float rating, String content, List<String> tags) {

        float oldRating = review.getRating();

        review.modify(rating, content,
                tags.stream().map(tagService::findByNameOrSave).toList());

        Book book = review.getBook();
        book.removeReviewRating(oldRating);
        book.addReviewRating(rating);
    }

    @Transactional
    public void deleteReview(long id) {

        Review review = findById(id);

        Book book = review.getBook();
        book.removeReviewRating(review.getRating());

        reviewRepository.deleteById(id);
    }
}
