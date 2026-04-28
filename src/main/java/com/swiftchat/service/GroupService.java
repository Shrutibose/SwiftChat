package com.swiftchat.service;

import com.swiftchat.dto.GroupMessage;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Set;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final StringRedisTemplate redis;

    // CREATE GROUP
    public String createGroup(String groupName, String createdBy) {
        String key = "group:" + groupName;
        if (redis.hasKey(key)) {
            return "Group already exists!";
        }
        redis.opsForSet().add(key, createdBy);
        return "Group " + groupName + " created successfully!";
    }

    // ADD MEMBER
    public String addMember(String groupName, String username) {
        String key = "group:" + groupName;
        if (!redis.hasKey(key)) {
            return "Group does not exist!";
        }
        redis.opsForSet().add(key, username);
        return username + " added to " + groupName;
    }

    // GET MEMBERS
    public Set<String> getMembers(String groupName) {
        return redis.opsForSet().members("group:" + groupName);
    }

    // IS MEMBER
    public boolean isMember(String groupName, String username) {
        return redis.opsForSet().isMember("group:" + groupName, username);
    }

    // SEND GROUP MESSAGE
    public String sendGroupMessage(String groupName, String from, String message) {
        if (!isMember(groupName, from)) {
            return "You are not a member of this group!";
        }
        try {
            GroupMessage groupMessage = new GroupMessage(
                    from, groupName, message,
                    java.time.LocalDateTime.now().toString()
            );
            String json = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(groupMessage);
            redis.opsForList().rightPush("groupchat:" + groupName, json);
            return "Message sent!";
        } catch (Exception e) {
            return "Error sending message!";
        }
    }

    // GET GROUP MESSAGES
    public List<GroupMessage> getGroupMessages(String groupName) {
        List<String> jsonList = redis.opsForList().range("groupchat:" + groupName, 0, -1);
        List<GroupMessage> messages = new ArrayList<>();

        if (jsonList != null) {
            for (String json : jsonList) {
                try {
                    GroupMessage msg = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(json, GroupMessage.class);
                    messages.add(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return messages;
    }
}