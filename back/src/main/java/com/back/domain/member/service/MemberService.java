package com.back.domain.member.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public Member findById(Long id) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (member.isDeleted()) {
            throw new ServiceException("404-1", "사용자를 찾을 수 없습니다.");
        }

        return member;
    }

    public Member findByUsername(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        if (member.isDeleted()) {
            throw new ServiceException("404-1", "사용자를 찾을 수 없습니다.");
        }
        return member;
    }

    public Member join(String username, String password, String nickname) {

        memberRepository.findByUsername(username)
                .ifPresent(_ -> {
                    throw new ServiceException("409-1", "이미 존재하는 아이디입니다.");
                });

        password = passwordEncoder.encode(password);
        return memberRepository.save(new Member(username, password, nickname));
    }

    public void delete(Long id) {
        Member member = findById(id);

        member.setDeletedDate(LocalDateTime.now());
    }

    public Optional<Member> findByRefreshToken(String apiKey) {
        return memberRepository.findByRefreshToken(apiKey);
    }

    public String genAccessToken(Member member) {
        return authTokenService.genAccessToken(member);
    }

    public Map<String, Object> payload(String accessToken) {
        return authTokenService.payload(accessToken);
    }

    public long count() {
        return memberRepository.count();
    }

    public void checkPassword(Member member, String password) {
        if (!passwordEncoder.matches(password, member.getPassword()))
            throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");

    }

    public Member findByGithubId(String githubId) throws NoSuchElementException {
        return memberRepository
                .findByGithubId(githubId)
                .orElseThrow(() ->
                        new NoSuchElementException("githubId가 %s인 회원을 찾을 수 없습니다."));
    }
}
