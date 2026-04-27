package com.swiftchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class ChatResponse {

    private String from;
    private String msg;
    private String time;

    public ChatResponse() {}

    public ChatResponse(String from, String msg, String time){
        this.from = from;
        this.msg = msg;
        this.time = time;
    }

    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
}