package com.back.domain.book.scheduler;

import com.back.domain.book.service.BookService;
import com.back.domain.book.service.BookViewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class BookViewsScheduler {

    private final RedisTemplate<String, String> redisTemplate;
    private final BookViewsService bookViewsService;

    @Scheduled(cron = "0 */5 * * * *")
    public void syncViewCountsToDb() {
        Set<String> viewKeys = redisTemplate.keys("book:view:*");

        if (viewKeys == null || viewKeys.isEmpty()) return;

        for (String key : viewKeys) {
            try {
                Long bookId = Long.parseLong(key.split(":")[2]);
                bookViewsService.updateViewCountInDb(bookId, bookViewsService.getViewCount(bookId));
            }
            catch (Exception e) {

            }
        }
    }

}
