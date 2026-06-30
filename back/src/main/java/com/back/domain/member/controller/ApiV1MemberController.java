package com.back.domain.member.controller;

import com.back.domain.member.dto.MemberDto;
import com.back.domain.member.dto.MemberWithUsernameAndWidgetLinkDto;
import com.back.domain.member.dto.MemberWithUsernameAndWidgetLinkDto;
import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Transactional(readOnly = true)
// @Tag(name = "ApiV1MemberController", description = "API 회원 컨트롤러");
// @SecurityRequirement(name = "bearerAuth")
public class ApiV1MemberController {
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping("/me")
    // @Operation("summary = '내 정보 조회")
    public MemberWithUsernameAndWidgetLinkDto me() {
        Member actor = memberService.findById(rq.getActor().getId());
        return new MemberWithUsernameAndWidgetLinkDto(actor);
    }

    @GetMapping("/{id}")
    // @Operation("summary = '회원 정보 조회")
    public MemberDto getUser(
            @PathVariable @Valid long id
    ) {
        Member member = memberService.findById(id);

        return new MemberDto(member);
    }

    public record MemberLoginReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String username,
            @NotBlank
            @Size(min = 2, max = 30)
            String password
    ) {
    }

    public record MemberLoginResBody(
            String accessToken,
            String refreshToken
    ) {
    }

    @DeleteMapping
    @Transactional
    // @Operation("summary = '회원 탈퇴")
    public RsData<Void> delete() {

        memberService.delete(rq.getActor().getId());

        rq.deleteCookie("refreshToken");
        rq.deleteCookie("accessToken");

        return new RsData<>("200-1", "회원 탈퇴 성공");
    }

    @PostMapping("/login")
    // @Operation("summary = '로그인")
    public RsData<MemberLoginResBody> login(
            @RequestBody @Valid MemberLoginReqBody reqBody
    ) {

        Member member = memberService.findByUsername(reqBody.username());

        memberService.checkPassword(member, reqBody.password());

        String accessToken = memberService.genAccessToken(member);

        rq.setCookie("refreshToken", member.getRefreshToken());
        rq.setCookie("accessToken", accessToken);

        return new RsData<>(
                "200-1",
                "로그인 성공",
                new MemberLoginResBody(
                        accessToken,
                        member.getRefreshToken()
                )
        );

    }

    public record MemberJoinReqBody(
            @NotBlank
            @Size(min = 2, max = 30)
            String username,
            @NotBlank
            @Size(min = 2, max = 30)
            String password,
            @NotBlank
            @Size(min = 2, max = 30)
            String githubId
    ) {
    }

    @PostMapping
    // @Operation("summary = '회원 가입")
    @Transactional
    public RsData<MemberLoginResBody> join(
            @RequestBody @Valid MemberJoinReqBody reqBody
    ) {

        Member member = memberService.join(
                reqBody.username(),
                reqBody.password(),
                reqBody.githubId()
        );

        memberService.checkPassword(member, reqBody.password());

        String accessToken = memberService.genAccessToken(member);

        rq.setCookie("refreshToken", member.getRefreshToken());
        rq.setCookie("accessToken", accessToken);

        return new RsData<>(
                "200-1",
                "회원가입 성공",
                new MemberLoginResBody(
                        accessToken,
                        member.getRefreshToken()
                )
        );

    }

    @DeleteMapping("/logout")
    // @Operation("summary = '로그아웃")
    public RsData<Void> logout() {
        rq.deleteCookie("refreshToken");
        rq.deleteCookie("accessToken");

        return new RsData<>("200-1", "로그아웃 성공");
    }

}
