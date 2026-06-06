package com.codequest.dto;

import java.time.OffsetDateTime;

public class RecentAttemptDto {
    private String title;
    private String status;
    private String color; // e.g., "green", "orange"
    private OffsetDateTime timestamp;

    public RecentAttemptDto(String title, String status, String color, OffsetDateTime timestamp) {
        this.title = title;
        this.status = status;
        this.color = color;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}
