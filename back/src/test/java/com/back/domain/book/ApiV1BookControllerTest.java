package com.back.domain.book;

import com.back.domain.book.entity.Book;
import com.back.domain.book.repository.BookRepository;
import com.back.domain.book.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApiV1BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("도서 다건 조회")
    void t1() throws Exception {

        List<Book> expectedBooks = bookRepository.findAll();

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/books"))
                .andDo(print());

        for (int i = 0; i < expectedBooks.size(); i++) {
            Book expected = expectedBooks.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(expected.getId()))
                    .andExpect(jsonPath("$[%d].title".formatted(i)).value(expected.getTitle()))
                    .andExpect(jsonPath("$[%d].imgUrl".formatted(i)).value(expected.getImgUrl()))
                    .andExpect(jsonPath("$[%d].averageRating".formatted(i)).value(expected.getAverageRating()));
        }
    }

    @Test
    @DisplayName("도서 단건 조회 - 비인증 사용자")
    void t2() throws Exception {

        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/books/%d".formatted(id)))
                .andDo(print());

        // Book book = bookService.findById(id).get();

        resultActions
                //.andExpect(handler().handlerType(ApiV1BookController.class))
                //.andExpect(handler().methodName("getBook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("book.getId()"))
                .andExpect(jsonPath("$.title").value("book.getTitle()"))
                .andExpect(jsonPath("$.description").value("book.getDescription()"))
                .andExpect(jsonPath("$.isbn").value("book.getIsbn()"))
                .andExpect(jsonPath("$.publishedDate").exists())
                .andExpect(jsonPath("$.publisher").value("book.getPublisher()"))
                .andExpect(jsonPath("$.imgUrl").value("book.getImgUrl()"))
                .andExpect(jsonPath("$.authors").isArray())
                .andExpect(jsonPath("$.reviewCount").exists())
                .andExpect(jsonPath("$.rating").exists())
                .andExpect(jsonPath("$.rating.average").exists())
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.isWished").doesNotExist()); // 비인증 시 isWished 없음
    }

    @Test
    @DisplayName("도서 단건 조회 - 인증 사용자 (isWished 포함)")
    @WithUserDetails("user1")
    void t3() throws Exception {

        Long id = 1L;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/books/%d".formatted(id)))
                .andDo(print());

        resultActions
                //.andExpect(handler().handlerType(ApiV1BookController.class))
                //.andExpect(handler().methodName("getBook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("book.getId()"))
                .andExpect(jsonPath("$.title").value("book.getTitle()"))
                .andExpect(jsonPath("$.isWished").isBoolean()); // 인증 시 isWished 있음
    }

    @Test
    @DisplayName("도서 검색")
    void t4() throws Exception {

        String searchTerm = "자바";

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/books/search")
                                .param("searchTerm", searchTerm))
                .andDo(print());

        // List<Book> books = bookService.search(searchTerm);

        resultActions
                //.andExpect(handler().handlerType(ApiV1BookController.class))
                //.andExpect(handler().methodName("search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].imgUrl").exists())
                .andExpect(jsonPath("$[0].averageRating").exists());
    }
}