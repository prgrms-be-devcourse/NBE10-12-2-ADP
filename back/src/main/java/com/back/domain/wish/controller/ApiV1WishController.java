package com.back.domain.wish.controller;

import com.back.domain.wish.service.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1WishController {
    private final WishService wishService;
}
