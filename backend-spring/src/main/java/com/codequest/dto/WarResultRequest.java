package com.codequest.dto;

import jakarta.validation.constraints.NotBlank;

public class WarResultRequest {

    @NotBlank
    private String matchId;

    private Long solveTimeMs;

    private Boolean won;

    private String language;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public Long getSolveTimeMs() {
        return solveTimeMs;
    }

    public void setSolveTimeMs(Long solveTimeMs) {
        this.solveTimeMs = solveTimeMs;
    }

    public Boolean getWon() {
        return won;
    }

    public void setWon(Boolean won) {
        this.won = won;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
