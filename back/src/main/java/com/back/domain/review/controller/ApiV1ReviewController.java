package com.back.domain.review.controller;

import com.back.domain.book.entity.Book;
import com.back.domain.book.repository.BookRepository;
import com.back.domain.book.service.BookService;
import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.review.dto.ReviewDto;
import com.back.domain.review.entity.Review;
import com.back.domain.review.service.ReviewService;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ApiV1ReviewController {
    private final ReviewService reviewService;
    private final BookRepository bookRepository;
    private final MemberService memberService;

    private final Rq rq;
    // private final BookService bookService;

    @GetMapping("/book/{bookId}")
    public List<ReviewDto> getReviewsByBook(
            @PathVariable @Valid long bookId
    ) {
        Book book = bookRepository.findById(bookId).get();

        return reviewService
                .findByBook(book)
                .stream()
                .map(ReviewDto::new)
                .toList();
    }

    public record ReviewsByMemberDto(
            Map<String, Object> rating,
            List<ReviewDto> results
    ) {

    }

    @GetMapping("/member/{memberId}")
    public ReviewsByMemberDto getReviewsByMember(
            @PathVariable @Valid long memberId
    ) {
        Member member = memberService.findById(memberId);

        return new ReviewsByMemberDto(
                reviewService.getRatingMap(member),
                reviewService
                    .findByMember(member)
                    .stream()
                    .map(ReviewDto::new)
                    .toList());
    }

    @GetMapping("/member/mine")
    public ReviewsByMemberDto mine() {
        return getReviewsByMember(rq.getActor().getId());
    }

    public record PostReviewsReqBody(
            @NotNull
            float rating,
            @NotBlank
            @Size(min = 2, max = 30)
            String content,
            @NotNull
            List<String> tags
    ) {

    }

    @PostMapping("/book/{bookId}")
    public RsData<ReviewDto> post(
            @PathVariable long bookId,
            @RequestBody @Valid PostReviewsReqBody req
    ) {
        Book book = bookRepository.findById(bookId).get();
        Member reviewer = memberService.findById(rq.getActor().getId());

        Review review = reviewService.addReview(
                book, reviewer,
                req.rating(), req.content(), req.tags());

        return new RsData<>(
                "201-1", "리뷰 작성 완료", new ReviewDto(review));

    }


    @PutMapping("/{id}")
    public RsData<ReviewDto> edit(
            @PathVariable long id,
            @RequestBody @Valid PostReviewsReqBody req
    ) {
        Review review = reviewService.findById(id);
        Member reviewer = memberService.findById(rq.getActor().getId());

        reviewService.editReview(review,
                req.rating(), req.content(), req.tags());

        return new RsData<>(
                "200-1", "리뷰 수정 완료", new ReviewDto(review));

    }

    @DeleteMapping("/{id}")
    public RsData<Void> delete(
            @PathVariable long id
    ) {
        reviewService.deleteReview(id);

        return new RsData<>(
                "200-1", "리뷰 삭제 완료");

    }

}
