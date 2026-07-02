package com.back.domain.review.controller;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.review.controller.ApiV1ReviewController;
import com.back.domain.review.entity.Review;
import com.back.domain.review.service.ReviewService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ApiV1ReviewControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("리뷰 다건 조회")
    void t1() throws Exception {

        long bookId = 1L;
        List<Review> reviews = List.of();

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/reviews/book/%d".formatted(bookId)))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ReviewController.class))
                .andExpect(handler().methodName("getReviewsByBook"))
                .andExpect(status().isOk());

        for (int i = 0; i < reviews.size(); i++) {
            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(0))
                    .andExpect(jsonPath("$[%d].rating".formatted(i)).value(""))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(""))
                    .andExpect(jsonPath("$[%d].modifiedDate".formatted(i)).value(""))
                    .andExpect(jsonPath("$[%d].createdDate".formatted(i)).value(""))
                    .andExpect(jsonPath("$[%d].reviewer".formatted(i)).exists())
                    .andExpect(jsonPath("$[%d].reviewer.id".formatted(i)).value(""))
                    .andExpect(jsonPath("$[%d].reviewer.githubId".formatted(i)).value(""))
                    .andExpect(jsonPath("$[%d].reviewer.githubLink".formatted(i)).value(""))
                    .andExpect(jsonPath("$[%d].tags".formatted(i)).exists());

            List<String> tags = reviews.get(i).getTags();

            for (int j = 0; j <  tags.size(); j++) {

                resultActions
                        .andExpect(jsonPath("$[%d].tags[%d]".formatted(i, j)).value("tags.get(j)"));

            }
        }
    }

    @Test
    @DisplayName("특정 회원이 작성한 리뷰 목록 조회")
    void t2() throws Exception {

        long memberId = 3L;
        Member member = memberService.findById(memberId);
        Map<String, Object> ratings = reviewService.getRatingMap(member);
        List<Review> reviews = reviewService.findByMember(member);

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/reviews/member/%d".formatted(memberId)))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ReviewController.class))
                .andExpect(handler().methodName("getReviewsByMember"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").exists())
                .andExpect(jsonPath("$.results").exists());

        for (var rating : ratings.entrySet()) {
            resultActions
                .andExpect(jsonPath("$.rating.[\"%s\"]".formatted(rating.getKey())).value(rating.getValue()));
        }

        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            resultActions
                    .andExpect(jsonPath("$.results.[%d].id".formatted(i)).value(review.getId()))
                    .andExpect(jsonPath("$.results[%d].rating".formatted(i)).value(review.getRating()))
                    .andExpect(jsonPath("$.results[%d].content".formatted(i)).value(review.getContent()))
                    .andExpect(jsonPath("$.results[%d].tags".formatted(i)).exists());

            List<String> tags = review.getTags();

            for (int j = 0; j <  tags.size(); j++) {
                resultActions
                        .andExpect(jsonPath("$.results[%d].tags[%d]".formatted(i, j)).value(tags.get(j)));
            }
        }
    }

    @Test
    @DisplayName("내가 작성한 리뷰 목록 조회")
    @WithUserDetails("user1")
    void t3() throws Exception {

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/reviews/member/mine"))
                .andDo(print());

        Member member = memberService.findByUsername("user1");
        Map<String, Object> ratings = reviewService.getRatingMap(member);
        List<Review> reviews = reviewService.findByMember(member);

        resultActions
                .andExpect(handler().handlerType(ApiV1ReviewController.class))
                .andExpect(handler().methodName("mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").exists())
                .andExpect(jsonPath("$.results").exists());


        for (var rating : ratings.entrySet()) {
            resultActions
                    .andExpect(jsonPath("$.rating.[\"%s\"]".formatted(rating.getKey())).value(rating.getValue()));
        }

        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            resultActions
                    .andExpect(jsonPath("$.results.[%d].id".formatted(i)).value(review.getId()))
                    .andExpect(jsonPath("$.results[%d].rating".formatted(i)).value(review.getRating()))
                    .andExpect(jsonPath("$.results[%d].content".formatted(i)).value(review.getContent()))
                    .andExpect(jsonPath("$.results[%d].tags".formatted(i)).exists());

            List<String> tags = review.getTags();

            for (int j = 0; j <  tags.size(); j++) {
                resultActions
                        .andExpect(jsonPath("$.results[%d].tags[%d]".formatted(i, j)).value(tags.get(j)));
            }
        }
    }

    @Test
    @DisplayName("리뷰 작성")
    @WithUserDetails("user2")
    void t4() throws Exception {
        long bookId = 1L;

        float rating = 3.5f;
        String content = "책 좋네요 ㅎㅎ";
        List<String> tags = List.of("a", "b");

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/reviews/book/%d".formatted(bookId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "rating": %.1f,
                                            "content": "%s",
                                            "tags": ["%s"]
                                        }
                                        """.formatted(rating, content, String.join("\", \"", tags)))
                )
                .andDo(print());

        Review review = reviewService.findLatest().get();

        resultActions
                .andExpect(handler().handlerType(ApiV1ReviewController.class))
                .andExpect(handler().methodName("post"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.message").value("리뷰 작성 완료"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(review.getId()))
                .andExpect(jsonPath("$.data.bookId").value(bookId))
                .andExpect(jsonPath("$.data.rating").value(rating))
                .andExpect(jsonPath("$.data.content").value(content))
                .andExpect(jsonPath("$.data.createdDate").value(Matchers.startsWith(review.getCreatedDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.tags").exists());

        for (int i = 0; i <  tags.size(); i++) {

            resultActions
                    .andExpect(jsonPath("$.data.tags[%d]".formatted(i)).value(tags.get(i)));

        }
    }

    @Test
    @DisplayName("리뷰 수정")
    @WithUserDetails("user1")
    void t5() throws Exception {
        long id = 1L;

        float rating = 5;
        String content = "다시 읽어보니 더 좋네요.";
        List<String> tags = List.of("a");

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/reviews/%d".formatted(id))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "rating": %.1f,
                                            "content": "%s",
                                            "tags": ["%s"]
                                        }
                                        """.formatted(rating, content, String.join("\", \"", tags)))
                )
                .andDo(print());

        Review review = reviewService.findById(id);

        resultActions
                .andExpect(handler().handlerType(ApiV1ReviewController.class))
                .andExpect(handler().methodName("edit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("리뷰 수정 완료"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.rating").value(rating))
                .andExpect(jsonPath("$.data.content").value(content))
                .andExpect(jsonPath("$.data.modifiedDate").value(Matchers.startsWith(review.getModifiedDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.tags").exists());

        for (int i = 0; i <  tags.size(); i++) {
            resultActions
                    .andExpect(jsonPath("$.data.tags[%d]".formatted(i)).value(tags.get(i)));

        }
    }

    @Test
    @DisplayName("리뷰 삭제")
    @WithUserDetails("user1")
    void t6() throws Exception {

        long reviewId = 1L;

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/reviews/%d".formatted(reviewId)))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ReviewController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("리뷰 삭제 완료"));

    }
}