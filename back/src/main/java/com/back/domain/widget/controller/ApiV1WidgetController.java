package com.back.domain.widget.controller;

import com.back.domain.widget.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/widgets")
@RequiredArgsConstructor
public class ApiV1WidgetController {
    private final WidgetService widgetService;

    public void getWidget() {
        // 구현 필요
    }
}
