package com.back.domain.tag.controller;

import com.back.domain.tag.service.TagService;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@Tag(name = "ApiV1TagController", description = "API 태그 컨트롤러")
public class ApiV1TagController {

    private final TagService tagService;

    public record TagPostReqBody(
            @NotBlank
            String name
    ) {

    }

    @PostMapping
    @Transactional
    @Operation(summary = "태그 추가")
    public RsData<Void> post(
            @RequestBody @Valid TagPostReqBody req
    ) {
        tagService.post(req.name());

        return new RsData<>(
                "201-1", "태그 생성 성공"
        );
    }

}
