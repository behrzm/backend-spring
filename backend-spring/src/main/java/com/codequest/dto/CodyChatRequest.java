package com.codequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class CodyChatRequest {

    @NotBlank
    @Size(max = 2000)
    private String message;

    @Size(max = 32)
    private String screenContext = "home";

    private List<CodyChatTurnDto> history = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getScreenContext() {
        return screenContext;
    }

    public void setScreenContext(String screenContext) {
        this.screenContext = screenContext;
    }

    public List<CodyChatTurnDto> getHistory() {
        return history;
    }

    public void setHistory(List<CodyChatTurnDto> history) {
        this.history = history != null ? history : new ArrayList<>();
    }
}
