package com.back.domain.book.repository;

import com.back.domain.book.entity.BookFetchProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFetchProgressRepository extends JpaRepository<BookFetchProgress, Long> {
}