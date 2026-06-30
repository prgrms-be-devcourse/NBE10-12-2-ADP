package com.back.domain.tag.controller;

import com.back.domain.tag.service.TagService;
import com.back.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class ApiV1TagController {

    private final TagService tagService;

    public record TagPostReqBody(
            @NotBlank
            String name
    ) {

    }

    @PostMapping
    public RsData<Void> post(
            @RequestBody @Valid TagPostReqBody req
    ) {
        tagService.post(req.name());

        return new RsData<>(
                "201-1", "태그 생성 성공"
        );
    }

}
