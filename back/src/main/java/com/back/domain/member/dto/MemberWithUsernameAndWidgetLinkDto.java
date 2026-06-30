package com.back.domain.member.dto;

import com.back.domain.member.entity.Member;

public record MemberWithUsernameAndWidgetLinkDto(
        Long id,
        String username,
        String githubId,
        String githubLink,
        String widgetLink
) {
    public MemberWithUsernameAndWidgetLinkDto(Member member) {
        this(member.getId(),
                member.getUsername(),
                member.getGithubId(),
                member.getGithubLink(),
                member.getWidgetLink());
    }
}
