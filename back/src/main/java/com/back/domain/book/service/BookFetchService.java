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

import java.time.LocalDateTime;
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

    private static final String API_URL = "https://apis.data.go.kr/1371029/BookInformationService_v2/getBookInfo";
    private static final int PAGE_SIZE = 20;
    private static final int DAILY_LIMIT = 10_000;

    private int callCount = 0;

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

        String url = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", progress.getCurrentPage())
                .queryParam("numOfRows", PAGE_SIZE)
                .queryParam("type", "json")
                .queryParam("label", "자바")
                .build(true)
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            JsonNode items = root.path("body").path("items").path("item");

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

            callCount++;
            progress.nextPage();
            bookFetchProgressRepository.save(progress);

            log.info("수집 완료 - apiKeyIndex: {}, page: {}, callCount: {}",
                    progress.getCurrentApiKeyIndex(), progress.getCurrentPage(), callCount);

            if (callCount >= DAILY_LIMIT) {
                log.info("API 키 {} 한도 소진. 다음 키로 전환합니다.", progress.getCurrentApiKeyIndex());
                progress.nextApiKeyIndex();
                bookFetchProgressRepository.save(progress);
                callCount = 0;
            }

        } catch (Exception e) {
            log.error("도서 수집 중 오류 발생 - page: {}", progress.getCurrentPage(), e);
        }
    }

    private void saveBook(JsonNode item) {
        String isbn = item.path("BIBO_isbn").asText();

        if (isbn.isBlank() || bookRepository.existsByIsbn(isbn)) {
            return;
        }

        String issuedYear = item.path("NLON_issuedYear").asText();
        LocalDateTime publishedDate = null;
        if (!issuedYear.isBlank()) {
            try {
                publishedDate = LocalDateTime.of(Integer.parseInt(issuedYear), 1, 1, 0, 0);
            } catch (NumberFormatException e) {
                log.warn("발행연도 파싱 실패: {}", issuedYear);
            }
        }

        Book book = new Book(
                item.path("DCTERMS_title").asText(),
                item.path("DCTERMS_abstract").asText(null),
                isbn,
                item.path("DC_creator").asText(null),
                publishedDate,
                item.path("DC_publisher").asText(null),
                null
        );

        bookRepository.save(book);
    }
}