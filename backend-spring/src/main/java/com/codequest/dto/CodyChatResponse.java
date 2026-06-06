package com.codequest.dto;

public class CodyChatResponse {

    private String reply;

    public CodyChatResponse() {
    }

    public CodyChatResponse(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
