package com.codequest.dto;

public class AddXpRequest {
    private Integer deltaXp;
    private String reason;

    public AddXpRequest() {
    }

    public AddXpRequest(Integer deltaXp, String reason) {
        this.deltaXp = deltaXp;
        this.reason = reason;
    }

    public Integer getDeltaXp() {
        return deltaXp;
    }

    public void setDeltaXp(Integer deltaXp) {
        this.deltaXp = deltaXp;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
