package com.back.global.initData;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {
    @Autowired
    @Lazy
    private BaseInitData self;
    private final MemberService memberService;

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
    }

}