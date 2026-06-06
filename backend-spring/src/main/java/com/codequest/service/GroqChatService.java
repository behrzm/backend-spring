package com.codequest.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class GroqChatService {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GroqChatService(
            @Value("${groq.api-key:}") String apiKey,
            @Value("${groq.model:llama-3.3-70b-versatile}") String model
    ) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = model;
        this.objectMapper = new ObjectMapper();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .build();
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String chat(String systemPrompt, List<GroqMessage> messages) throws IOException {
        if (!isConfigured()) {
            throw new IllegalStateException("GROQ_API_KEY is not configured on the server");
        }

        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", model);
        root.put("temperature", 0.75);
        root.put("max_tokens", 700);

        ArrayNode msgArray = root.putArray("messages");
        ObjectNode system = msgArray.addObject();
        system.put("role", "system");
        system.put("content", systemPrompt);

        for (GroqMessage message : messages) {
            ObjectNode node = msgArray.addObject();
            node.put("role", message.role());
            node.put("content", message.content());
        }

        Request request = new Request.Builder()
                .url(GROQ_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(objectMapper.writeValueAsString(root), JSON))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new IOException("Groq API error " + response.code() + ": " + body);
            }
            JsonNode json = objectMapper.readTree(body);
            JsonNode content = json.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.asText().isBlank()) {
                throw new IOException("Groq returned empty content");
            }
            return content.asText().trim();
        }
    }

    public record GroqMessage(String role, String content) {
    }
}
