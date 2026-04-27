package com.swiftchat.controller;

import com.swiftchat.dto.ChatListResponse;
import com.swiftchat.dto.ChatResponse;
import com.swiftchat.service.MessageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//handles HTTP req
@RestController
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    //sends msg
    @PostMapping("/send")
    public String send(@RequestParam String from,
                       @RequestParam String to,
                       @RequestParam String message) {
        return service.send(from, to, message);
    }

    //receives msg
    @GetMapping("/receive")
    public List<ChatResponse> receive(@RequestParam String from,
                                      @RequestParam String to) {
        return service.receive(from, to);
    }

    // shows the chat connections of any user
    @GetMapping("/chats")
    public List<ChatListResponse> getChats(@RequestParam String user) {
        return service.getChats(user);
    }

    //LAST MESSAGE API
    @GetMapping("/last-message")
    public String lastMessage(@RequestParam String user1,
                              @RequestParam String user2) {
        return service.getLastMessage(user1, user2);
    }
    @Controller
    public class HomeController {

        @GetMapping("/")
        public String home() {
            return "chat"; // OR "checked" depending on file name
        }
    }
}

// URL - http://localhost:8081/