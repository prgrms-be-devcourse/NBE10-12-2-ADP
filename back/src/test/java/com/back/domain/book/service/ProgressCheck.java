package com.back.domain.book.service;

import com.back.domain.book.entity.Book;
import com.back.domain.book.entity.BookFetchProgress;
import com.back.domain.book.repository.BookFetchProgressRepository;
import com.back.domain.book.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("prod")
@SpringBootTest
public class ProgressCheck {

    @Autowired
    private BookFetchProgressRepository repo;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void check() {
        for (BookFetchProgress p : repo.findAll()) {
            System.out.println("PROGRESS id=" + p.getId() + " page=" + p.getCurrentPage()
                    + " keyIdx=" + p.getCurrentApiKeyIndex() + " dailyCount=" + p.getDailyCallCount()
                    + " lastCallDate=" + p.getLastCallDate());
        }
        System.out.println("BOOK_COUNT=" + bookRepository.count());
        List<Book> sample = bookRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"))).getContent();
        for (Book b : sample) {
            System.out.println("SAMPLE id=" + b.getId() + " isbn=" + b.getIsbn() + " title=" + b.getTitle()
                    + " description=" + b.getDescription());
        }
    }
}