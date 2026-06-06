package com.codequest.dto;

public class FriendRequestAction {
    private String senderId;
    private String action; // ACCEPT, DECLINE

    public FriendRequestAction() {}

    public FriendRequestAction(String senderId, String action) {
        this.senderId = senderId;
        this.action = action;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
