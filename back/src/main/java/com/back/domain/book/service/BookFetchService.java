package com.back.domain.book.service;

import com.back.domain.book.entity.Book;
import com.back.domain.book.entity.BookFetchProgress;
import com.back.domain.book.repository.BookFetchProgressRepository;
import com.back.domain.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookFetchService {

    private final BookRepository bookRepository;
    private final BookFetchProgressRepository bookFetchProgressRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${custom.book-fetch.api-keys}")
    private List<String> apiKeys;

    private static final String API_URL = "https://www.nl.go.kr/seoji/SearchApi.do";
    private static final int PAGE_SIZE = 500;
    private static final int DAILY_LIMIT = 10_000;

    @Transactional
    public void fetch() {
        BookFetchProgress progress = bookFetchProgressRepository.findById(1L)
                .orElseGet(() -> {
                    BookFetchProgress newProgress = new BookFetchProgress();
                    newProgress.nextPage();
                    return bookFetchProgressRepository.save(newProgress);
                });

        if (progress.getCurrentApiKeyIndex() >= apiKeys.size()) {
            log.info("오늘 모든 API 키 호출 한도 소진. 내일 다시 시작합니다.");
            return;
        }

        String apiKey = apiKeys.get(progress.getCurrentApiKeyIndex());

        // 국립중앙도서관 api 요청변수에 맞게 변경
        String url = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("cert_Key", apiKey)
                .queryParam("result_style", "json")
                .queryParam("page_no", progress.getCurrentPage())
                .queryParam("page_size", PAGE_SIZE)
                .queryParam("sort", "INPUT_DATE")
                .queryParam("order_by", "DESC")
                .build(true)
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            JsonNode items = root.path("docs");

            if (items.isMissingNode() || items.isEmpty()) {
                log.info("더 이상 수집할 데이터가 없습니다. page: {}", progress.getCurrentPage());
                return;
            }

            if (items.isArray()) {
                for (JsonNode item : items) {
                    saveBook(item);
                }
            } else {
                saveBook(items);
            }

            progress.incrementCallCount();
            progress.nextPage();
            bookFetchProgressRepository.save(progress);

            log.info("수집 완료 - apiKeyIndex: {}, page: {}, callCount: {}",
                    progress.getCurrentApiKeyIndex(), progress.getCurrentPage(), progress.getDailyCallCount());

            if (progress.getDailyCallCount() >= DAILY_LIMIT) {
                log.info("API 키 {} 한도 소진. 다음 키로 전환합니다.", progress.getCurrentApiKeyIndex());
                progress.nextApiKeyIndex();
                bookFetchProgressRepository.save(progress);
            }

        } catch (Exception e) {
            log.error("도서 수집 중 오류 발생 - page: {}", progress.getCurrentPage(), e);
        }
    }

    private void saveBook(JsonNode item) {
        String isbn = item.path("EA_ISBN").asText();

        if (isbn.isBlank() || bookRepository.existsByIsbn(isbn)) {
            return;
        }

        String publishPredate = item.path("PUBLISH_PREDATE").asText();
        LocalDateTime publishedDate = null;
        if (!publishPredate.isBlank()) {
            try {
                if (publishPredate.length() == 8) {
                    LocalDate date = LocalDate.parse(publishPredate, DateTimeFormatter.ofPattern("yyyyMMdd"));
                    publishedDate = date.atStartOfDay();
                } else if (publishPredate.length() >= 4) {
                    publishedDate = LocalDateTime.of(Integer.parseInt(publishPredate.substring(0, 4)), 1, 1, 0, 0);
                }
            } catch (Exception e) {
                log.warn("발행일 파싱 실패: {}", publishPredate);
            }
        }

        Book book = new Book(
                item.path("TITLE").asText(),
                item.path("BOOK_INTRODUCTION_URL").asText(null),
                isbn,
                item.path("AUTHOR").asText(null),
                publishedDate,
                item.path("PUBLISHER").asText(null),
                item.path("TITLE_URL").asText(null)
        );

        bookRepository.save(book);
    }
}