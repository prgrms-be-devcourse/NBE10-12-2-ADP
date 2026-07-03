package com.back.domain.book.service;

import com.back.domain.book.dto.BookDto;
import com.back.domain.member.entity.Member;
import com.back.domain.review.entity.Review;
import com.back.domain.review.service.ReviewService;
import com.back.standard.recommend.byReview.RecommendByReview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.back.standard.recommend.byReview.RecommendByReview.RecommendReview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookRecommendService {
    private final RecommendByReview recommendSystem = new RecommendByReview();
    private final ReviewService reviewService;
    private final BookService bookService;

    private RecommendReview reviewToRecommendReview(Review review) {
        return new RecommendReview(
                review.getReviewer().getId(),
                review.getBook().getId(),
                review.getRating());
    }

    private List<RecommendReview> recommendReviewsByReviewer(Member reviewer) {

        return reviewService.getByMember(reviewer, 0, 5)
                .stream()
                .map(this::reviewToRecommendReview)
                .toList();

    }

    public List<BookDto> getRecommends(Member actor) {

        recommendSystem.clear();

        List<Review> recentReviews = reviewService
                .getByMember(actor, 0, 5)
                .stream()
                .toList();

        recommendSystem.setData(
                recommendReviewsByReviewer(actor));

        Set<Member> members = new HashSet<>();

        for (var review : recentReviews)
            reviewService.getByBookId(review.getBook().getId(), 0, 10)
                    .stream()
                    .forEach(r -> members.add(r.getReviewer()));

        for (var reviewer: members) {
            recommendSystem.setData(
                    recommendReviewsByReviewer(reviewer));
        }

        return recommendSystem.getRecommend(actor.getId(), 5, 10)
                .stream()
                .map(id -> new BookDto(bookService.getPureBook(id)))
                .toList();
    }

}
