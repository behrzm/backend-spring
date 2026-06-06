package com.codequest.dto;

public class WarResultResponse {

    private boolean won;
    private int eloDelta;
    private int newElo;
    private int totalWins;
    private String playerTime;
    private String opponentTime;

    public WarResultResponse() {
    }

    public WarResultResponse(boolean won, int eloDelta, int newElo, int totalWins, String playerTime, String opponentTime) {
        this.won = won;
        this.eloDelta = eloDelta;
        this.newElo = newElo;
        this.totalWins = totalWins;
        this.playerTime = playerTime;
        this.opponentTime = opponentTime;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public int getEloDelta() {
        return eloDelta;
    }

    public void setEloDelta(int eloDelta) {
        this.eloDelta = eloDelta;
    }

    public int getNewElo() {
        return newElo;
    }

    public void setNewElo(int newElo) {
        this.newElo = newElo;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public String getPlayerTime() {
        return playerTime;
    }

    public void setPlayerTime(String playerTime) {
        this.playerTime = playerTime;
    }

    public String getOpponentTime() {
        return opponentTime;
    }

    public void setOpponentTime(String opponentTime) {
        this.opponentTime = opponentTime;
    }
}
