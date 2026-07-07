package com.back.domain.book.service;

import com.back.domain.book.entity.Book;
import com.back.domain.book.repository.BookRepository;
import com.back.global.rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookViewsService {

    private final Rq rq;
    private final RedisTemplate<String, String> redisTemplate;
    private final BookRepository bookRepository;

    public int getViewCount(Long bookId) {
        return redisTemplate.opsForZSet()
                .score("viewCount", bookId.toString())
                .intValue();
    }

    public List<Long> topViewed() {
        return redisTemplate.opsForZSet()
                .reverseRangeWithScores("viewCount", 0, 10)
                .stream()
                .map(a -> Long.parseLong(a.getValue().toString()))
                .toList();
    }

    public void incrementViewCount(Long bookId) {
        redisTemplate.opsForZSet().incrementScore("viewCount", bookId.toString(), 1);
    }

    @Transactional
    public void updateViewCountInDb(Long bookId, int viewCount) {
        Optional<Book> bookOp = bookRepository.findById(bookId);
        if (bookOp.isEmpty()) return;

        bookOp.get().setViewCount(viewCount);
    }
}
