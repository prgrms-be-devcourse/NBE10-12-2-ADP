package com.back.domain.review.dto;

import com.back.domain.member.dto.MemberDto;
import com.back.domain.review.entity.Review;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewDto(
        long id,
        long bookId,
        float rating,
        String content,
        LocalDateTime modifiedDate,
        LocalDateTime createdDate,
        MemberDto reviewer,
        List<String> tags
) {
    public ReviewDto(Review review) {
        this(
                review.getId(),
                review.getBook().getId(),
                review.getRating(),
                review.getContent(),
                review.getModifiedDate(),
                review.getCreatedDate(),
                new MemberDto(review.getReviewer()),
                review.getTags());
    }
}
