package com.codequest.dto;

import com.google.firebase.database.annotations.Nullable;

public class UpdateProfileRequest {
    private String displayName;
    private String avatarUrl;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String displayName, @Nullable String avatarUrl) {
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
