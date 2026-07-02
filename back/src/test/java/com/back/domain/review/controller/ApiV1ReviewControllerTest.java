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

}