package com.back.domain.home.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class HomeController {
    @GetMapping("/health")
    @ResponseStatus(OK)
    public void healthCheck() { /* 헬스체크 */ }
}
