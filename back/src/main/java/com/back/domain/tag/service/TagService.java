package com.back.domain.tag.service;

import com.back.domain.tag.entity.Tag;
import com.back.domain.tag.repository.TagRepository;
import com.back.global.exception.ServiceException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public void post(String name) {

        Optional<Tag> tag = tagRepository.findByName(name);

        if (tag.isPresent()) {
            throw new ServiceException("409-1", "이미 존재하는 태그입니다.");
        }

        tagRepository.save(new Tag(name));
    }

    public Tag findByNameOrSave(String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> tagRepository.save(new Tag(name)));
    }
}
