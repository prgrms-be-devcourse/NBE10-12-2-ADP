package com.back.domain.wish.controller;

import com.back.domain.book.entity.Book;
import com.back.domain.book.repository.BookRepository;
import com.back.domain.book.service.BookService;
import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.wish.service.WishService;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishes")
@RequiredArgsConstructor
public class ApiV1WishController {

    private final WishService wishService;
    private final MemberService memberService;
    private final BookRepository bookRepository;

    private final Rq rq;

    public record BookDto(
            long id,
            String title,
            String imgUrl
    ) {
        public BookDto(Book book) {
            this(book.getId(), book.getTitle(), book.getImgUrl());
        }
    }

    @GetMapping("/mine")
    public List<BookDto> getWishes() {
        Member actor = memberService.findById(rq.getActor().getId());

        return wishService
                .findByMember(actor)
                .stream()
                .map(wish -> new BookDto(wish.getBook()))
                .toList();
    }

    @PostMapping("/book/{id}")
    @Transactional
    public RsData<Void> addWish(
            @PathVariable @Valid long id
    ) {

        wishService.addWish(memberService.findById(rq.getActor().getId()),
                bookRepository.findById(id).get());

        return new RsData<>(
                "201-1",
                "찜 추가 성공"
        );
    }

    @DeleteMapping("/book/{id}")
    @Transactional
    public RsData<Void> deleteWish(
            @PathVariable @Valid long id
    ) {

        wishService.deleteWish(memberService.findById(rq.getActor().getId()),
                bookRepository.findById(id).get());

        return new RsData<>(
                "200-1",
                "찜 삭제 성공"
        );
    }

}
