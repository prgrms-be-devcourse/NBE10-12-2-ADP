package com.back.global.initData;

import com.back.domain.book.entity.Book;
import com.back.domain.book.repository.BookRepository;
import com.back.domain.book.service.BookService;
import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    @Autowired
    @Lazy
    private BaseInitData self;
    private final MemberService memberService;
    private final BookRepository bookRepository;
    private final WishService wishService;
    private final BookService bookService;

    @Bean
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        if (memberService.count() > 0) return;

        Member memberSystem = memberService.join("system", "1234", "시스템");
        memberSystem.modifyRefreshToken(memberSystem.getUsername());
        memberSystem.modifyRefreshToken(memberSystem.getUsername());

        Member memberAdmin = memberService.join("admin", "1234", "관리자");
        memberAdmin.modifyRefreshToken(memberAdmin.getUsername());
        memberAdmin.modifyRefreshToken(memberAdmin.getUsername());

        Member memberUser1 = memberService.join("user1", "1234", "유저1");
        memberUser1.modifyRefreshToken(memberUser1.getUsername());
        memberUser1.modifyRefreshToken(memberUser1.getUsername());

        Member memberUser2 = memberService.join("user2", "1234", "유저2");
        memberUser2.modifyRefreshToken(memberUser2.getUsername());
        memberUser2.modifyRefreshToken(memberUser2.getUsername());

        Member memberUser3 = memberService.join("user3", "1234", "유저3");
        memberUser3.modifyRefreshToken(memberUser3.getUsername());
        memberUser3.modifyRefreshToken(memberUser3.getUsername());

        Book book1 = bookRepository.save(new Book(
                "책제목", "책설명", "isbn1", "작가",
                LocalDateTime.now(), "출판사", ""));
        Book book2 = bookRepository.save(new Book(
                "책제목2", "책설명", "isbn2", "작가",
                LocalDateTime.now(), "출판사", ""));

        Book book3 = bookRepository.save(new Book(
                "책제목3", "책설명", "isbn3", "작가",
                LocalDateTime.now(), "출판사", ""));

        wishService.addWish(memberUser1, book1);
        wishService.addWish(memberUser2, book2);
        wishService.addWish(memberUser3, book3);
    }

}