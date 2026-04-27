package com.swiftchat.service;

import com.swiftchat.model.ChatMessage;
import com.swiftchat.dto.ChatResponse;
import com.swiftchat.dto.ChatListResponse;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MessageService {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    // =========================
    // SEND MESSAGE
    // =========================
    public String send(String from, String to, String message) {

        String isLoggedIn = redis.opsForValue().get("login:" + from);

        if (isLoggedIn == null) {
            return "User not logged in";
        }

        String key = getChatKey(from, to);

        try {
            ChatMessage chatMessage =
                    new ChatMessage(from, to, message, LocalDateTime.now().toString()); //creating java obj

            String json = objectMapper.writeValueAsString(chatMessage); //converting obj -> string

            redis.opsForList().rightPush(key, json); //pushing that string to redis

        } catch (Exception e) {
            return "Error while sending message";
        }

        return "sent";
    }

    // =========================
    // RECEIVE FULL CHAT
    // =========================
    public List<ChatResponse> receive(String from, String to) {

        String key = getChatKey(from, to);

        List<String> messages = redis.opsForList().range(key, 0, -1);

        List<ChatResponse> responseList = new ArrayList<>();

        if (messages != null) {
            for (String json : messages) {

                try {
                    ChatMessage msg =
                            objectMapper.readValue(json, ChatMessage.class);

                    responseList.add(
                            new ChatResponse(
                                    msg.getFrom(),
                                    msg.getMsg(),
                                    msg.getTime()
                            )
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return responseList;
    }

    // =========================
    // CHAT KEY GENERATION
    // =========================
    private String getChatKey(String user1, String user2) {
        if (user1.compareTo(user2) < 0) {
            return "chat:" + user1 + ":" + user2;
        } else {
            return "chat:" + user2 + ":" + user1;
        }
    }

    // =========================
    // CHAT LIST (HOME SCREEN)
    // =========================
    public List<ChatListResponse> getChats(String user) {

        Set<String> keys = redis.keys("chat:*");
        List<ChatListResponse> responseList = new ArrayList<>();

        if (keys != null) {
            for (String key : keys) {

                String users = key.replace("chat:", "");
                String[] parts = users.split(":");

                String user1 = parts[0];
                String user2 = parts[1];

                String otherUser = null;

                if (user.equals(user1)) {
                    otherUser = user2;
                } else if (user.equals(user2)) {
                    otherUser = user1;
                }

                if (otherUser != null) {

                    Long size = redis.opsForList().size(key);
                    String lastMsg = "";

                    if (size != null && size > 0) {
                        try {
                            String json = redis.opsForList().index(key, size - 1);

                            ChatMessage msg =
                                    objectMapper.readValue(json, ChatMessage.class);

                            lastMsg = msg.getMsg();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    responseList.add(
                            new ChatListResponse(otherUser, lastMsg)
                    );
                }
            }
        }

        return responseList;
    }

    // =========================
    // LAST MESSAGE
    // =========================
    public String getLastMessage(String user1, String user2) {

        String key = getChatKey(user1, user2);

        Long size = redis.opsForList().size(key);

        if (size == null || size == 0) {
            return "No messages";
        }

        try {
            String json = redis.opsForList().index(key, size - 1);

            ChatMessage msg =
                    objectMapper.readValue(json, ChatMessage.class);

            return msg.getFrom() + ": " + msg.getMsg();

        } catch (Exception e) {
            return "Error reading message";
        }
    }
}

/* When a user sends a message, the system first checks if the sender is logged in.
If not, it returns an error.
If yes, it generates a unique chat key for both users.
Then it creates a ChatMessage object containing sender, receiver, message, and time.
This object is converted into a JSON string.
Finally, the JSON message is appended to a Redis list using rightPush. */


/* receive() gets the chat key (same logic as send),
fetches all messages under that key from Redis,
creates a response list,
loops through each JSON string,
converts each string into a ChatMessage object,
then converts that into a ChatResponse and adds it to the list,
handles exceptions, and finally returns the list. */


// getChatKey() -> generates 1 unique chatkey for 2 users


 /* getChatList() -> When a user tries to fetch the chat list, the frontend sends a request to the controller. The controller calls the getChats() method.
Then, we fetch all chat keys from Redis using the "chat:*" pattern.
We create a response list that will be returned to the frontend.
After that, we perform a null check and iterate over each key.
For every key, we remove the "chat:" prefix and split the remaining string to extract the users.
Then, we check whether the current user is part of that chat or not.
If yes, we identify the other user.
After that, we fetch the last message from the Redis list, convert it from JSON into an object,
and finally add it to the response list. */


// getLastMessage() -> It returns the latest message between two users
/* When a user requests the last message between two users, we first generate the unique chat key using both users.
Then we check how many messages exist in that Redis list.
If the size is null or zero, we return “No messages found”.
Otherwise, we directly fetch only the last message using size - 1 index (not all messages).
That message is stored as JSON, so we convert it into a ChatMessage object.
Finally, we format and return the last message, while handling any exceptions. */