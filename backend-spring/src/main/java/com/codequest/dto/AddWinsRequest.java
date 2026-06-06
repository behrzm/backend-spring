package com.codequest.dto;

public class AddWinsRequest {
    private Integer deltaWins = 1;

    public AddWinsRequest() {
    }

    public AddWinsRequest(Integer deltaWins) {
        this.deltaWins = deltaWins;
    }

    public Integer getDeltaWins() {
        return deltaWins;
    }

    public void setDeltaWins(Integer deltaWins) {
        this.deltaWins = deltaWins;
    }
}
