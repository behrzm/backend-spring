package com.codequest.service;

import com.codequest.dto.CodyChatRequest;
import com.codequest.dto.CodyChatTurnDto;
import com.codequest.util.LanguageDetect;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CodyCompanionService {

    private static final String SYSTEM_PROMPT_EN = """
            You are Cody, a friendly AI coding companion in the CodeQuest learning app.
            Reply in English by default. Keep answers short (2–5 sentences), warm and motivating.
            Give practical tips on learning, picking a language, practicing code, and gamification.
            Do not invent facts about the user. If unsure, say so and suggest a next step.
            """;

    private static final String SYSTEM_PROMPT_RU = """
            Ты Cody — дружелюбный ИИ-напарник в приложении CodeQuest для обучения программированию.
            Отвечай на русском языке, кратко (2–5 предложений), с теплом и мотивацией.
            Давай практические советы по учёбе, выбору языка, практике кода и геймификации.
            Не выдумывай факты о пользователе. Если не знаешь ответ — честно скажи и предложи шаг.
            """;

    private final GroqChatService groqChatService;

    public CodyCompanionService(GroqChatService groqChatService) {
        this.groqChatService = groqChatService;
    }

    public String chat(CodyChatRequest request) throws IOException {
        String screen = request.getScreenContext() != null ? request.getScreenContext() : "home";
        boolean russian = LanguageDetect.isPrimarilyRussian(request.getMessage());
        String basePrompt = russian ? SYSTEM_PROMPT_RU : SYSTEM_PROMPT_EN;
        String contextual = basePrompt + "\nApp screen: " + screenLabel(screen) + ".";

        List<GroqChatService.GroqMessage> messages = new ArrayList<>();
        for (CodyChatTurnDto turn : request.getHistory()) {
            String role = normalizeRole(turn.getRole());
            if (role != null && turn.getContent() != null && !turn.getContent().isBlank()) {
                messages.add(new GroqChatService.GroqMessage(role, turn.getContent().trim()));
            }
        }
        messages.add(new GroqChatService.GroqMessage("user", request.getMessage().trim()));

        return groqChatService.chat(contextual, messages);
    }

    private String normalizeRole(String role) {
        if (role == null) return null;
        return switch (role.toLowerCase()) {
            case "user", "assistant", "system" -> role.toLowerCase();
            case "cody" -> "assistant";
            case "player" -> "user";
            default -> "user";
        };
    }

    private String screenLabel(String screen) {
        return switch (screen.toLowerCase()) {
            case "languages" -> "language selection";
            case "play" -> "practice / play hub";
            case "stats" -> "progress statistics";
            case "home" -> "home";
            default -> screen;
        };
    }
}
