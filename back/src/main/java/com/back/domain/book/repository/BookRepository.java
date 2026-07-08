package com.back.domain.book.repository;

import com.back.domain.book.dto.BookDto;
import com.back.domain.book.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);

    List<Book> findByTitleContaining(String searchTerm);

    Page<Book> findAllByOrderByAverageRatingDesc(Pageable pageable);

    Page<Book> findAllByOrderByReviewCountDesc(Pageable pageable);
}
