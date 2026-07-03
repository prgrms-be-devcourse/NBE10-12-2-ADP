package com.back.domain.book.service;

import com.back.domain.book.dto.BookDto;
import com.back.domain.member.entity.Member;
import com.back.domain.review.service.ReviewService;
import com.back.standard.recommend.byReview.CosineSimilarityRecommendByReview;
import com.back.standard.recommend.byReview.RecommendByReview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookRecommendService {
    private final RecommendByReview recommendSystem = new CosineSimilarityRecommendByReview();
    private final ReviewService reviewService;
    private final BookService bookService;

    public List<BookDto> getRecommends(Member actor) {
        recommendSystem.setData(reviewService.getPureReviewAll()
                .stream().map(review -> new RecommendByReview
                        .RecommendReview(review.getReviewer().getId(),
                        review.getBook().getId(), review.getRating())));

        return recommendSystem.getRecommend(actor.getId(), 5, 10)
                .map(id -> new BookDto(bookService.getPureBook(id)))
                .toList();
    }

}
