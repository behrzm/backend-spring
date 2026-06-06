package com.codequest.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateFriendChallengeRequest {

    @NotBlank
    private String targetUserId;

    @NotBlank
    private String language;

    public String getTargetUserId() { return targetUserId; }
    public void setTargetUserId(String targetUserId) { this.targetUserId = targetUserId; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
