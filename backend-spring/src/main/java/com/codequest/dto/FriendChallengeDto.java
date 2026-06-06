package com.codequest.dto;

public class FriendChallengeDto {
    private String challengeId;
    private String challengerId;
    private String challengerName;
    private String targetId;
    private String language;
    private String status;
    private Integer challengerElo;
    private Integer challengerXp;

    public FriendChallengeDto() {
    }

    public FriendChallengeDto(
            String challengeId,
            String challengerId,
            String challengerName,
            String targetId,
            String language,
            String status,
            Integer challengerElo,
            Integer challengerXp
    ) {
        this.challengeId = challengeId;
        this.challengerId = challengerId;
        this.challengerName = challengerName;
        this.targetId = targetId;
        this.language = language;
        this.status = status;
        this.challengerElo = challengerElo;
        this.challengerXp = challengerXp;
    }

    public String getChallengeId() { return challengeId; }
    public void setChallengeId(String challengeId) { this.challengeId = challengeId; }
    public String getChallengerId() { return challengerId; }
    public void setChallengerId(String challengerId) { this.challengerId = challengerId; }
    public String getChallengerName() { return challengerName; }
    public void setChallengerName(String challengerName) { this.challengerName = challengerName; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getChallengerElo() { return challengerElo; }
    public void setChallengerElo(Integer challengerElo) { this.challengerElo = challengerElo; }
    public Integer getChallengerXp() { return challengerXp; }
    public void setChallengerXp(Integer challengerXp) { this.challengerXp = challengerXp; }
}
