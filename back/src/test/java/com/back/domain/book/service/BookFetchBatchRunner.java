package com.back.domain.book.service;

import com.back.domain.book.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("prod")
@SpringBootTest
public class BookFetchBatchRunner {

    @Autowired
    private BookFetchService bookFetchService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void run() throws InterruptedException {
        int calls = 200;
        long delayMs = 300;

        for (int i = 0; i < calls; i++) {
            bookFetchService.fetch();
            Thread.sleep(delayMs);
        }

        System.out.println("TOTAL_BOOKS=" + bookRepository.count());
    }
}