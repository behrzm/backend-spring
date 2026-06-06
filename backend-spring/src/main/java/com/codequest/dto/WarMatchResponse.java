package com.codequest.dto;

public class WarMatchResponse {
    private String matchId;
    private String opponentName;
    private Integer opponentElo;
    private Integer missionId;
    private Long opponentSolveTimeMs;
    private Integer playerElo;
    private Boolean botOpponent;

    public WarMatchResponse() {}

    public WarMatchResponse(
            String matchId,
            String opponentName,
            Integer opponentElo,
            Integer missionId,
            Long opponentSolveTimeMs,
            Integer playerElo,
            Boolean botOpponent
    ) {
        this.matchId = matchId;
        this.opponentName = opponentName;
        this.opponentElo = opponentElo;
        this.missionId = missionId;
        this.opponentSolveTimeMs = opponentSolveTimeMs;
        this.playerElo = playerElo;
        this.botOpponent = botOpponent;
    }

    public String getMatchId() { return matchId; }
    public void setMatchId(String matchId) { this.matchId = matchId; }
    public String getOpponentName() { return opponentName; }
    public void setOpponentName(String opponentName) { this.opponentName = opponentName; }
    public Integer getOpponentElo() { return opponentElo; }
    public void setOpponentElo(Integer opponentElo) { this.opponentElo = opponentElo; }
    public Integer getMissionId() { return missionId; }
    public void setMissionId(Integer missionId) { this.missionId = missionId; }
    public Long getOpponentSolveTimeMs() { return opponentSolveTimeMs; }
    public void setOpponentSolveTimeMs(Long opponentSolveTimeMs) { this.opponentSolveTimeMs = opponentSolveTimeMs; }
    public Integer getPlayerElo() { return playerElo; }
    public void setPlayerElo(Integer playerElo) { this.playerElo = playerElo; }
    public Boolean getBotOpponent() { return botOpponent; }
    public void setBotOpponent(Boolean botOpponent) { this.botOpponent = botOpponent; }
}
