package com.back.domain.member;

import com.back.domain.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApiV1MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("내 정보 조회")
    @WithUserDetails("user1")
    void t1() throws Exception {

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/members/me"))
                .andDo(print());

        // Member member = memberService.findByUsername("user1").get();

        resultActions
                //.andExpect(handler().handlerType(ApiV1MemberController.class))
                //.andExpect(handler().methodName("me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("member.getId()"))
                .andExpect(jsonPath("$.username").value("member.getUsername()"))
                .andExpect(jsonPath("$.githubId").value("member.getGithubId()"))
                .andExpect(jsonPath("$.githubLink").value("member.getGithubLink()"))
                .andExpect(jsonPath("$.widgetLink").value("member.getWidgetLink()"));

    }

    @Test
    @DisplayName("회원 정보 조회")
    void t2() throws Exception {

        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/members/%d".formatted(id)))
                .andDo(print());

        // Member member = memberService.findById(id).get();

        resultActions
                //.andExpect(handler().handlerType(ApiV1MemberController.class))
                //.andExpect(handler().methodName("getUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("member.getId()"))
                .andExpect(jsonPath("$.githubId").value("member.getGithubId()"))
                .andExpect(jsonPath("$.githubLink").value("member.getGithubLink()"));

    }

    @Test
    @DisplayName("회원 탈퇴")
    @WithUserDetails("user1")
    void t3() throws Exception {

        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/members"))
                .andDo(print());

        resultActions
                //.andExpect(handler().handlerType(ApiV1MemberController.class))
                //.andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("회원 탈퇴 성공"));

    }

    @Test
    @DisplayName("로그인")
    void t4() throws Exception {

        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """))
                .andDo(print());

        // Member member = memberService.findByUsername("user1").get();

        resultActions
                //.andExpect(handler().handlerType(ApiV1MemberController.class))
                //.andExpect(handler().methodName("login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").value("member.getRefreshToken()"));

        resultActions.andExpect(
                result -> {
                    Cookie refreshTokenCookie = result.getResponse().getCookie("refreshToken");

                    assertThat(refreshTokenCookie.getValue()).isEqualTo("member.getRefreshToken()");
                    assertThat(refreshTokenCookie.getPath()).isEqualTo("/");
                    assertThat(refreshTokenCookie.isHttpOnly()).isTrue();

                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");

                    assertThat(accessTokenCookie.getValue()).isNotBlank();
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();
                }
        );

    }


    @Test
    @DisplayName("회원가입")
    void t5() throws Exception {

        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234",
                                            "githubId": ""
                                        }
                                        """))
                .andDo(print());

        // Member member = memberService.findByUsername("user1").get();

        resultActions
                //.andExpect(handler().handlerType(ApiV1MemberController.class))
                //.andExpect(handler().methodName("join"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("회원가입 성공"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").value("member.getRefreshToken()"));

        resultActions.andExpect(
                result -> {
                    Cookie refreshTokenCookie = result.getResponse().getCookie("refreshToken");

                    assertThat(refreshTokenCookie.getValue()).isEqualTo("member.getRefreshToken()");
                    assertThat(refreshTokenCookie.getPath()).isEqualTo("/");
                    assertThat(refreshTokenCookie.isHttpOnly()).isTrue();

                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");

                    assertThat(accessTokenCookie.getValue()).isNotBlank();
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();
                }
        );

    }

    @Test
    @DisplayName("로그아웃")
    @WithUserDetails("user1")
    void t6() throws Exception {

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/members/logout"))
                .andDo(print());

        resultActions
                //.andExpect(handler().handlerType(ApiV1MemberController.class))
                //.andExpect(handler().methodName("logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("로그아웃 성공"));

        resultActions.andExpect(
                result -> {
                    Cookie refreshTokenCookie = result.getResponse().getCookie("refreshToken");

                    assertThat(refreshTokenCookie.getValue()).isEmpty();
                    assertThat(refreshTokenCookie.getMaxAge()).isEqualTo(0);
                    assertThat(refreshTokenCookie.getPath()).isEqualTo("/");
                    assertThat(refreshTokenCookie.isHttpOnly()).isTrue();

                    Cookie accessTokenCookie = result.getResponse().getCookie("accessToken");

                    assertThat(accessTokenCookie.getValue()).isEmpty();
                    assertThat(accessTokenCookie.getMaxAge()).isEqualTo(0);
                    assertThat(accessTokenCookie.getPath()).isEqualTo("/");
                    assertThat(accessTokenCookie.isHttpOnly()).isTrue();
                }
        );

    }
}