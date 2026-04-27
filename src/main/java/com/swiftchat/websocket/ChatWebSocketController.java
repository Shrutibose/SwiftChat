package com.swiftchat.websocket;

import com.swiftchat.model.ChatMessage;
import com.swiftchat.service.MessageService;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload; //takes incoming msg data and converts it into java object
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller //this tells the spring that this class will handle all websocket messages
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate; //used by server to send msgs to client via topics
    private final MessageService service;

    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate,
                                   MessageService service) {
        this.messagingTemplate = messagingTemplate;
        this.service = service;
    }

    // client sends message here, eg- /app/send
    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessage message) {

        // add timestamp
        message.setTime(LocalDateTime.now().toString());

        // save to Redis (reuse your service)
        service.send(message.getFrom(), message.getTo(), message.getMsg());

        // send message to receiver (All users subscribed to /topic/chat receive it)
        messagingTemplate.convertAndSend(
                "/topic/messages/" + message.getTo(), message); //goes to the particular user
    }
}

/*
Client connects to /chat and a WebSocket connection is established (FRONTEND)
A WebSocket session is created for that client.
Client sends a message to /app/send.
Spring receives the message through the WebSocket connection.
Spring removes the /app prefix.
It finds the method with @MessageMapping("/send").
The controller method is executed.
The message is converted into a Java object using @Payload.
Backend processes the message.
adds timestamp.
saves message (Redis/DB).
Server sends the message using SimpMessagingTemplate.
Message is sent to a destination like /topic/messages/{user}.
Message broker checks all sessions subscribed to that topic.
Broker delivers the message to those sessions.
Subscribed clients receive the message instantly.
*/