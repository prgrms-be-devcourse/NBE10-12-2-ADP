package com.back.domain.book.service;

import com.back.domain.book.dto.BookDetailDto;
import com.back.domain.book.dto.BookDto;
import com.back.domain.book.entity.Book;
import com.back.domain.book.repository.BookRepository;
import com.back.domain.member.entity.Member;
import com.back.domain.review.entity.Review;
import com.back.domain.review.repository.ReviewRepository;
import com.back.domain.wish.repository.WishRepository;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final WishRepository wishRepository;

    public List<BookDto> getBooks() {
        return bookRepository.findAll().stream()
                .map(book -> new BookDto(book, book.getAverageRating()))
                .toList();
    }

    public BookDetailDto getBook(Long id, Member actor) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 도서입니다"));

        Map<String, Object> ratingMap = buildRatingMap(book);

        List<Review> reviews = reviewRepository.findByBook(book);
        List<String> tags = reviews.stream()
                .flatMap(r -> r.getTags().stream())
                .distinct()
                .toList();

        Boolean isWished = null;
        if (actor != null) {
            isWished = wishRepository.findByMemberAndBook(actor, book).isPresent();
        }

        return new BookDetailDto(book, isWished, ratingMap, tags);
    }

    private Map<String, Object> buildRatingMap(Book book) {
        Map<String, Object> ratingMap = new HashMap<>();
        ratingMap.put("average", book.getAverageRating());
        for (int i = 1; i <= 10; i++) {
            float rating = i * 0.5f;
            ratingMap.put("%.1f".formatted(rating), reviewRepository.countByBookAndRating(book, rating));
        }
        return ratingMap;
    }
}
