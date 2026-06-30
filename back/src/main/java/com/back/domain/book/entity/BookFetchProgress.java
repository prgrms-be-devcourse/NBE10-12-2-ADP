package com.back.domain.book.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class BookFetchProgress extends BaseEntity {

    private int currentPage;
    private int currentApiKeyIndex;

    public void nextPage() {
        this.currentPage++;
    }

    public void nextApiKeyIndex() {
        this.currentApiKeyIndex++;
    }
}
