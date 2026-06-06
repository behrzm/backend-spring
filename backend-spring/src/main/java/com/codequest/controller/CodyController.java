package com.codequest.controller;

import com.codequest.dto.CodyChatRequest;
import com.codequest.dto.CodyChatResponse;
import com.codequest.service.CodyCompanionService;
import com.codequest.service.GroqChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/companion")
@Tag(name = "Cody Companion", description = "AI companion chat powered by Groq")
@SecurityRequirement(name = "bearerAuth")
public class CodyController {

    private final CodyCompanionService codyCompanionService;
    private final GroqChatService groqChatService;

    public CodyController(CodyCompanionService codyCompanionService, GroqChatService groqChatService) {
        this.codyCompanionService = codyCompanionService;
        this.groqChatService = groqChatService;
    }

    @PostMapping("/chat")
    @Operation(summary = "Send a message to Cody and receive an AI reply")
    public ResponseEntity<?> chat(@Valid @RequestBody CodyChatRequest request) {
        if (!groqChatService.isConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "GROQ_API_KEY is not configured on the server"));
        }
        try {
            String reply = codyCompanionService.chat(request);
            return ResponseEntity.ok(new CodyChatResponse(reply));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Groq request failed: " + e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
