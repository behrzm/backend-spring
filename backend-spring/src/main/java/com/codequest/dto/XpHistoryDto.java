package com.codequest.dto;

import com.codequest.domain.entity.XpHistory;
import java.time.OffsetDateTime;

public class XpHistoryDto {
    private Long id;
    private String userId;
    private Integer amount;
    private String reason;
    private OffsetDateTime createdAt;

    public XpHistoryDto() {
    }

    public XpHistoryDto(Long id, String userId, Integer amount, String reason, OffsetDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public static XpHistoryDto from(XpHistory xpHistory) {
        return new XpHistoryDto(
                xpHistory.getId(),
                xpHistory.getUserId(),
                xpHistory.getAmount(),
                xpHistory.getReason(),
                xpHistory.getCreatedAt()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

