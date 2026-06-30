package com.back.domain.review;

import com.back.domain.review.controller.ApiV1ReviewController;
import com.back.domain.review.entity.Review;
import com.back.domain.review.service.ReviewService;
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

            List<String> tags = List.of(); //reviews.get(i).getTags();

            for (int j = 0; j <  tags.size(); j++) {

                resultActions
                        .andExpect(jsonPath("$[%d].tags[%d]".formatted(i, j)).value("tags.get(j)"));

            }
        }
    }

    @Test
    @DisplayName("특정 회원이 작성한 리뷰 목록 조회")
    void t2() throws Exception {

        long memberId = 1L;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/reviews/member/%d".formatted(memberId)))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ReviewController.class))
                .andExpect(handler().methodName("getReviewsByMember"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").exists())
                .andExpect(jsonPath("$.rating.average").value(""))
                .andExpect(jsonPath("$.rating.[\"0.0\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"0.5\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"1.0\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"1.5\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"2.0\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"2.5\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"3.0\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"3.5\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"4.0\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"4.5\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"5.0\"]").value(""))
                .andExpect(jsonPath("$.results").exists());

        List<Review> reviews = List.of();

        for (int i = 0; i < reviews.size(); i++) {
            resultActions
                    .andExpect(jsonPath("$.results.[%d].id".formatted(i)).value(0))
                    .andExpect(jsonPath("$.results[%d].rating".formatted(i)).value(""))
                    .andExpect(jsonPath("$.results[%d].content".formatted(i)).value(""))
                    .andExpect(jsonPath("$.results[%d].tags".formatted(i)).exists());

            List<String> tags = List.of(); //reviews.get(i).getTags();

            for (int j = 0; j <  tags.size(); j++) {

                resultActions
                        .andExpect(jsonPath("$[%d].tags[%d]".formatted(i, j)).value("tags.get(j)"));

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

        resultActions
                .andExpect(handler().handlerType(ApiV1ReviewController.class))
                .andExpect(handler().methodName("mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").exists())
                .andExpect(jsonPath("$.rating.average").value(""))
                .andExpect(jsonPath("$.rating.[\"0.0\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"0.5\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"1.0\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"1.5\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"2.0\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"2.5\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"3.0\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"3.5\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"4.0\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"4.5\"]").value(""))
                .andExpect(jsonPath("$.rating.[\"5.0\"]").value(""))
                .andExpect(jsonPath("$.results").exists());

        List<Review> reviews = List.of();

        for (int i = 0; i < reviews.size(); i++) {
            resultActions
                    .andExpect(jsonPath("$.results.[%d].id".formatted(i)).value(0))
                    .andExpect(jsonPath("$.results[%d].rating".formatted(i)).value(""))
                    .andExpect(jsonPath("$.results[%d].content".formatted(i)).value(""))
                    .andExpect(jsonPath("$.results[%d].tags".formatted(i)).exists());

            List<String> tags = List.of(); //reviews.get(i).getTags();

            for (int j = 0; j <  tags.size(); j++) {

                resultActions
                        .andExpect(jsonPath("$[%d].tags[%d]".formatted(i, j)).value("tags.get(j)"));

            }
        }
    }

    @Test
    @DisplayName("리뷰 작성")
    @WithUserDetails("user1")
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
                                            "rating": %.1f
                                            "content": %s,
                                            "tags": ["%s"]
                                        }
                                        """.formatted(rating, content, String.join("\", \"", tags)))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ReviewController.class))
                .andExpect(handler().methodName("post"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.message").value("리뷰 작성 완료"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(""))
                .andExpect(jsonPath("$.data.bookId").value(""))
                .andExpect(jsonPath("$.data.rating").value(rating))
                .andExpect(jsonPath("$.data.content").value(content))
                .andExpect(jsonPath("$.data.createdDate").value(""))
                .andExpect(jsonPath("$.data.tags").exists());


        List<String> retTags = List.of(); //reviews.get(i).getTags();

        for (int i = 0; i <  retTags.size(); i++) {

            resultActions
                    .andExpect(jsonPath("$data.tags[%d]".formatted(i)).value(retTags.get(i)));

        }
    }

    @Test
    @DisplayName("리뷰 수정")
    @WithUserDetails("user1")
    void t5() throws Exception {
        long bookId = 1L;

        float rating = 5;
        String content = "다시 읽어보니 더 좋네요.";
        List<String> tags = List.of("a", "b");

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/reviews/book/%d".formatted(bookId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "rating": %.1f
                                            "content": %s,
                                            "tags": ["%s"]
                                        }
                                        """.formatted(rating, content, String.join("\", \"", tags)))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1ReviewController.class))
                .andExpect(handler().methodName("edit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.message").value("리뷰 수정 완료"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.rating").value(rating))
                .andExpect(jsonPath("$.data.content").value(content))
                .andExpect(jsonPath("$.data.modifiedDate").value(""))
                .andExpect(jsonPath("$.data.tags").exists());

        List<String> retTags = List.of(); //reviews.get(i).getTags();

        for (int i = 0; i <  retTags.size(); i++) {

            resultActions
                    .andExpect(jsonPath("$data.tags[%d]".formatted(i)).value(retTags.get(i)));

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