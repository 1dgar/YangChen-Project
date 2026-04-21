package com.agri.controller;

import com.agri.service.AiService;
import com.agri.vo.Result;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody ChatRequest request) {
        String response = aiService.chat(request.getMessage(), request.getHistory());
        return Result.success(response);
    }

    @PostMapping("/chat-with-context")
    public Result<String> chatWithContext(@RequestBody ChatWithContextRequest request) {
        String response = aiService.chatWithContext(request.getMessage(), request.getContext());
        return Result.success(response);
    }

    @Data
    public static class ChatRequest {
        private String message;
        private String history;
    }

    @Data
    public static class ChatWithContextRequest {
        private String message;
        private String context;
    }
}
