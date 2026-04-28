package com.swiftchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessage {
    private String from;
    private String groupName;
    private String message;
    private String time;
}