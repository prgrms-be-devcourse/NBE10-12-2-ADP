package com.back.domain.member.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import com.back.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthTokenService authTokenService;
    private final PasswordEncoder passwordEncoder;

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }
    public Optional<Member> findByUsername(String username) { return memberRepository.findByUsername(username); }
    public Member join(String username, String password, String nickname) {

        findByUsername(username)
                .ifPresent(_ -> {
                    throw new ServiceException("409-1", "이미 존재하는 아이디입니다.");
                });

        password = passwordEncoder.encode(password);
        return memberRepository.save(new Member(username, password, nickname));
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
}
