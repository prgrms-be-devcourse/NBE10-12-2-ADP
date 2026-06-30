package com.back.domain.member.dto;

import com.back.domain.member.entity.Member;

public record MemberDto(
        Long id,
        String githubId,
        String githubLink
) {
    public MemberDto(Member member) {
        this(
                member.getId(),
                member.getGithubId(),
                member.getGithubLink()
        );
    }
}
