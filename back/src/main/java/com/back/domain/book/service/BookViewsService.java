package com.back.domain.book.service;

import com.back.domain.book.dto.BookDto;
import com.back.domain.book.entity.Book;
import com.back.domain.book.repository.BookRepository;
import com.back.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookViewsService {

    private final Rq rq;
    private final RedisTemplate<String, String> redisTemplate;
    private final BookRepository bookRepository;
    private final BookService bookService;

    private int getViewCount(Long bookId, String key) {
        Double score = redisTemplate.opsForZSet()
                .score(key, bookId.toString());

        return score == null ? 0 : score.intValue();
    }

    private String getMinuteKey() {
        return "viewCount:minute:%s:".formatted(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
    }

    public int getViewCount(Long bookId) {
        return getViewCount(bookId, "viewCount");
    }

    public List<BookDto> topViewed(int page, int size) {

        int start = page * size;
        int end = start + size - 1;

        return redisTemplate.opsForZSet()
                .reverseRangeWithScores("viewCount", start, end)
                .stream()
                .map(a ->
                        new BookDto(bookService.getPureBook(Long.parseLong(a.getValue()))))
                .toList();
    }

    public List<BookDto> topViewedInLastHour(int page, int size) {

        int start = page * size;
        int end = start + size - 1;

        LocalDateTime now = LocalDateTime.now();

        List<String> keys = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            String key = "viewCount:minute:%s".formatted(
                    now.minusMinutes(i)
                            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
            );

            keys.add(key);
        }

        String tempKey = "viewCount:lastHour:temp";

        redisTemplate.delete(tempKey);

        redisTemplate.opsForZSet().unionAndStore(
                keys.getFirst(),
                keys.subList(1, keys.size()),
                tempKey
        );

        redisTemplate.expire(tempKey, Duration.ofSeconds(10));

        Set<String> bookIds = redisTemplate.opsForZSet()
                .reverseRange(tempKey, start, end);

        if (bookIds == null || bookIds.isEmpty()) {
            return List.of();
        }

        return bookIds.stream()
                .map(bookId -> new BookDto(bookService.getPureBook(Long.parseLong(bookId))))
                .toList();
    }

    public void incrementViewCount(Long bookId) {
        redisTemplate.opsForZSet().incrementScore("viewCount", bookId.toString(), 1);

        String minuteKey = getMinuteKey();
        redisTemplate.opsForZSet().incrementScore(
                minuteKey, bookId.toString(), 1);
        redisTemplate.expire(minuteKey, Duration.ofMinutes(70));
        ;
    }

    @Transactional
    public void updateViewCountInDb() {
        Set<String> viewKeys = redisTemplate.opsForZSet().range("viewCount", 0, -1);

        if (viewKeys == null || viewKeys.isEmpty()) return;

        for (String key : viewKeys) {
            try {
                Long bookId = Long.parseLong(key.split(":")[2]);
                updateViewCountInDb(bookId, getViewCount(bookId));
            }
            catch (Exception e) {

            }
        }
    }

    @Transactional
    public void updateViewCountInDb(Long bookId, int viewCount) {
        Optional<Book> bookOp = bookRepository.findById(bookId);
        if (bookOp.isEmpty()) return;

        bookOp.get().setViewCount(viewCount);
    }
}
