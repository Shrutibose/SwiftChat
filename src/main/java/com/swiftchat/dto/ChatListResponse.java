package com.swiftchat.dto;

public class ChatListResponse {

    private String user;
    private String lastMessage;

    public ChatListResponse() {}

    public ChatListResponse(String user, String lastMessage) {
        this.user = user;
        this.lastMessage = lastMessage;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}