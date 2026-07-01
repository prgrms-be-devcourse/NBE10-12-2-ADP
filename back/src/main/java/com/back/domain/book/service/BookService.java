package com.back.domain.book.service;

import com.back.domain.book.dto.BookDto;
import com.back.domain.book.repository.BookRepository;
import com.back.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public List<BookDto> getBooks() {
        return bookRepository.findAll().stream()
                .map(book -> new BookDto(book, book.getAverageRating()))
                .toList();
    }
}
